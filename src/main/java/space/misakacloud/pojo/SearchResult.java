package space.misakacloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class SearchResult implements IWsResponse {
    private static final int BATCH_SIZE = 3;

    public enum Type {
        FILE, FOLDER
    }

    public enum FileType {
        // 文本文件
        TXT(".txt", "Text File"),
        XML(".xml", "XML File"),
        JSON(".json", "JSON File"),
        CSV(".csv", "CSV File"),
        LOG(".log", "Log File"),
        MD(".md", "Markdown File"),

        // 图像文件
        JPG(".jpg", "JPEG Image"),
        JPEG(".jpeg", "JPEG Image"),
        PNG(".png", "PNG Image"),
        GIF(".gif", "GIF Image"),
        BMP(".bmp", "Bitmap Image"),
        SVG(".svg", "SVG Image"),
        TIF(".tif", "TIFF Image"),
        TIFF(".tiff", "TIFF Image"),
        ICO(".ico", "Icon Image"),
        PSD(".psd", "Photoshop File"),

        // 视频文件
        MP4(".mp4", "MP4 Video"),
        AVI(".avi", "AVI Video"),
        MKV(".mkv", "MKV Video"),
        MOV(".mov", "MOV Video"),
        WMV(".wmv", "WMV Video"),
        FLV(".flv", "FLV Video"),
        WEBM(".webm", "WEBM Video"),

        // 音频文件
        MP3(".mp3", "MP3 Audio"),
        WAV(".wav", "WAV Audio"),
        FLAC(".flac", "FLAC Audio"),
        AAC(".aac", "AAC Audio"),
        OGG(".ogg", "OGG Audio"),
        WMA(".wma", "WMA Audio"),
        M4A(".m4a", "M4A Audio"),

        // 压缩文件
        ZIP(".zip", "ZIP Archive"),
        RAR(".rar", "RAR Archive"),
        TAR(".tar", "TAR Archive"),
        GZ(".gz", "GZIP Archive"),
        BZ2(".bz2", "BZIP2 Archive"),
        _7Z(".7z", "7-Zip Archive"),
        ISO(".iso", "ISO Disk Image"),

        // 文档文件
        PDF(".pdf", "PDF Document"),
        DOC(".doc", "Microsoft Word Document"),
        DOCX(".docx", "Microsoft Word Document"),
        XLS(".xls", "Microsoft Excel Spreadsheet"),
        XLSX(".xlsx", "Microsoft Excel Spreadsheet"),
        PPT(".ppt", "Microsoft PowerPoint Presentation"),
        PPTX(".pptx", "Microsoft PowerPoint Presentation"),
        ODT(".odt", "OpenDocument Text"),
        ODS(".ods", "OpenDocument Spreadsheet"),
        ODP(".odp", "OpenDocument Presentation"),

        // 编程/代码文件
        JAVA(".java", "Java Source File"),
        C(".c", "C Source File"),
        CPP(".cpp", "C++ Source File"),
        H(".h", "C/C++ Header File"),
        PY(".py", "Python Script"),
        JS(".js", "JavaScript File"),
        HTML(".html", "HTML File"),
        CSS(".css", "CSS File"),
        PHP(".php", "PHP File"),
        RB(".rb", "Ruby Script"),
        GO(".go", "Go Source File"),
        SWIFT(".swift", "Swift Source File"),

        // 数据库/配置文件
        SQL(".sql", "SQL File"),
        DB(".db", "Database File"),
        SQLITE(".sqlite", "SQLite Database File"),
        INI(".ini", "Configuration File"),
        CFG(".cfg", "Configuration File"),
        YAML(".yaml", "YAML Configuration File"),
        YML(".yml", "YAML Configuration File"),

        // 可执行文件
        EXE(".exe", "Windows Executable"),
        DLL(".dll", "Dynamic Link Library"),
        DMG(".dmg", "Mac Disk Image"),
        APP(".app", "Mac Application"),
        SH(".sh", "Shell Script"),
        BAT(".bat", "Batch File"),

        UNKNOWN("unknown","unknown");
        private final String extension;
        private final String description;
        public enum FileForm{
            VIDEO,
            MUSIC,
            PROGRAM,
            TEXT,
            COMPRESS,
            UNKNOWN;

            public static final FileType[] videotags=new FileType[]{MP4,AVI,MKV,MOV,WMV,FLV,WEBM};
            public static final FileType[] musictags=new FileType[]{MP3,WAV,FLAC,AAC,OGG,WMA,M4A};
            public static final FileType[] programtags=new FileType[]{JAVA,C,CPP,H,PY,JS,HTML,CSS,PHP,RB,GO,SWIFT};
            public static final FileType[] texttags=new FileType[]{TXT,XML,JSON,CSV,LOG,MD};
            public static final FileType[] compresstags=new FileType[]{ZIP,RAR,TAR,GZ,BZ2,_7Z,ISO};
        }
        FileType(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        public static FileForm getForm(String name){
            int i = name.lastIndexOf(".");
            if(i!=-1){
                FileType t = fromString(name.substring(i));
                if(Arrays.stream(FileForm.videotags).anyMatch(a->a.extension.equals(t.extension))){
                    return FileForm.VIDEO;
                }else if(Arrays.stream(FileForm.musictags).anyMatch(a->a.extension.equals(t.extension))){
                    return FileForm.MUSIC;
                }else if(Arrays.stream(FileForm.programtags).anyMatch(a->a.extension.equals(t.extension))){
                    return FileForm.PROGRAM;
                }else if(Arrays.stream(FileForm.texttags).anyMatch(a->a.extension.equals(t.extension))){
                    return FileForm.TEXT;
                }else if(Arrays.stream(FileForm.compresstags).anyMatch(a->a.extension.equals(t.extension))){
                    return FileForm.COMPRESS;
                }
            }
            return FileForm.UNKNOWN;
        }
        // 根据字符串获取对应的枚举值
        public static FileType fromString(String ext) {
            for (FileType type : FileType.values()) {
                if (type.getExtension().equalsIgnoreCase(ext)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        public String getExtension() {
            return extension;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return extension + " (" + description + ")";
        }
    }



    private String name;
    private String path;
    private long size;
    private long time;
    private Type type;
    private FileType fileType;
    private FileType.FileForm fileForm;
}
