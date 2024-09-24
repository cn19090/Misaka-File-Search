package space.misakacloud.pojo;

import java.io.File;

@FunctionalInterface
public interface Predict {
    boolean predict(File f);

}
