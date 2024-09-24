package space.misakacloud.Handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import space.misakacloud.pojo.Predict;
import space.misakacloud.pojo.SearchResult;
import space.misakacloud.pojo.TreeNode;
import space.misakacloud.pojo.WsResponse;
import space.misakacloud.socket.DataSocket;
import space.misakacloud.socket.MainSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class LocalSearch extends Thread {
    private MainSocket mainSocket;
    private File root;
    private Predict predict;
    private String sessionId;
    private static TreeNode<SearchResult> cache = new TreeNode<>(null, new LinkedList<>());

    @Override
    public void run() {
        search(root, predict, sessionId);
    }

    public void stopSearch() {
        HandlerThread.pool.forEach(a -> a.stopFlag = true);
        while (!HandlerThread.isDone()) {
            yield();
        }
    }

    public static void openFile(String path) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public InnSearchResult search(File root, Predict predict, String sessionId) {
        LinkedList<File> queue = new LinkedList<>();

        queue.push(root);
        HandlerThread.firstThread(queue, predict, sessionId, mainSocket).start();
        while (!HandlerThread.isDone()) {
            yield();
        }
        synchronized (HandlerThread.resultList) {
            mainSocket.send(
                    new WsResponse(
                            sessionId,
                            WsResponse.Type.SEARCH_RESULT,
                            HandlerThread.resultList.stream().map(a -> new SearchResult(
                                                    a.getName(),
                                                    a.getAbsolutePath(),
                                                    a.length(),
                                                    a.lastModified(),
                                                    a.isFile() ? SearchResult.Type.FILE : SearchResult.Type.FOLDER,
                                                    SearchResult.FileType.fromString(a.getName().substring(a.getName().lastIndexOf("."))),
                                                    SearchResult.FileType.getForm(a.getName())
                                            )
                                    )
                                    .collect(Collectors.toList())
                    )
            );
            HandlerThread.resultList.clear();
        }
        return new InnSearchResult(HandlerThread.getUsingTime() / 1000d, HandlerThread.resultList);
    }

}


class InnSearchResult {
    double usingTime;
    LinkedList<File> resultList;

    public InnSearchResult(double usingTime, LinkedList<File> resultList) {
        this.usingTime = usingTime;
        this.resultList = resultList;
    }
}

class HandlerThread extends Thread {

    public long hid = (long) (Math.random() * 1000000000000L);
    /**
     * 线程池
     */
    public static Collection<HandlerThread> pool = Collections.synchronizedCollection(new LinkedList<>());
    /**
     * 筛选条件函数
     */
    private Predict predict;

    /**
     * 结果序列
     */
    public static LinkedList<File> resultList = new LinkedList<>();
    /**
     * 多线程遍历树的最大同时存在线程数
     */
    public static final int MAX_THREAD_NUM = 10;
    public static final int MAX_BATCH_SIZE = 10;
    public static final int NEXT_BATCH_SEND_MAX_WAIT_TIME = 2;
    public static long last_finded_time;
    private static MainSocket mainSocket;
    private String sessionId;

    private LinkedList<File> queue;

    private static long startTimestamp;
    private static long completeTimestamp;

    public boolean stopFlag = false;

    public static Java2DFrameConverter converter = new Java2DFrameConverter();

    public void sendResult() {
        ArrayList<File> buffer = new ArrayList<>();
        synchronized (resultList) {
            last_finded_time = System.currentTimeMillis();

            buffer.addAll(resultList);
            resultList.clear();
        }
        List<SearchResult> collect = buffer.stream().map(a -> new SearchResult(
                                a.getName(),
                                a.getAbsolutePath(),
                                a.isFile() ? a.length() : -1,
                                a.lastModified(),
                                a.isFile() ? SearchResult.Type.FILE : SearchResult.Type.FOLDER,
                                a.getName().lastIndexOf(".") != -1 ? SearchResult.FileType.fromString(a.getName().substring(a.getName().lastIndexOf("."))) : null,
                                SearchResult.FileType.getForm(a.getName())
                        )
                )
                .collect(Collectors.toList());
        mainSocket.send(
                new WsResponse(
                        sessionId,
                        WsResponse.Type.SEARCH_RESULT,
                        collect
                )
        );
        new Thread(() -> {
            List<HashMap<Object, Object>> c = collect.stream().filter(a -> SearchResult.FileType.getForm(a.getName()).equals(SearchResult.FileType.FileForm.VIDEO)).map(f -> {

                HashMap<Object, Object> map = new HashMap<>();
                try {
                    System.out.println(f.getPath());
                    FrameGrabber grabber = new FFmpegFrameGrabber(f.getPath());
                    grabber.start();
                    Frame grab = grabber.grab();
                    while (grab.imageHeight == 0) {
                        grab = grabber.grab();
                    }
                    BufferedImage convert = converter.convert(grab);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(convert, "png", out);
                    String bs = Base64.getEncoder().encodeToString(out.toByteArray());
                    map.put("path", f.getPath());
                    map.put("thumbnail", bs);
                    grabber.stop();


                } catch (Exception e) {

                }

                return map;
            }).collect(Collectors.toList());
            WsResponse response = new WsResponse(null, WsResponse.Type.THUMBNAIL, c);
            DataSocket.send(response);
        }).start();

    }

    @Override
    public void run() {

        while (!queue.isEmpty() && !stopFlag) {
            File pop = queue.pop();

            LinkedList<File> folders = new LinkedList<>();
            File[] listFiles = pop.listFiles();
            if (System.currentTimeMillis() - last_finded_time > NEXT_BATCH_SEND_MAX_WAIT_TIME * 1000 && resultList.size() > 0) {
                sendResult();
            }
            if (listFiles != null) {
                new Thread(() -> {
                    Arrays.stream(listFiles).filter((f) -> predict.predict(f)).forEach(f -> {
//                        System.out.println(f.getAbsolutePath());
                        synchronized (resultList) {
                            resultList.add(f);
                        }
                        if (resultList.size() >= MAX_BATCH_SIZE) {
                            sendResult();
                        }
                    });
                }).start();
                Arrays.stream(listFiles).forEach(a -> {

                    if (a.isFile()) {
//                        resultList.push(a);
                    } else if (a.isDirectory()) {
                        folders.push(a);
                    }
                });

                if (pool.size() < MAX_THREAD_NUM) {
                    new HandlerThread(folders, predict, sessionId).start();
                } else {
                    queue.addAll(folders);
                }

            } else {

            }

        }

        try {
            pool.removeIf(a -> a.hid == this.hid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        completeTimestamp = System.currentTimeMillis();

    }

    public static boolean isDone() {
        return pool.isEmpty();
    }

    public static int getCurrentThreadNum() {
        return pool.size();
    }

    public static long getUsingTime() {
        return completeTimestamp - startTimestamp;
    }

    public static long currentNodeNum() {
        return resultList.size();
    }

    private HandlerThread() {
    }

    public static HandlerThread firstThread(LinkedList<File> queue, Predict predict, String sessionId, MainSocket socket) {

        HandlerThread r = new HandlerThread(queue, predict, sessionId);
        pool.add(r);
        startTimestamp = System.currentTimeMillis();
        mainSocket = socket;
        return r;
    }

    public HandlerThread(LinkedList<File> queue, Predict predict, String sessionId) {
        pool.add(this);
        this.queue = queue;
        this.predict = predict;
        this.sessionId = sessionId;
    }
}
