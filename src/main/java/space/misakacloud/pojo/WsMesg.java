package space.misakacloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class WsMesg {
    public enum Type {
        MOVE, STOP, WINDOWS_SMALL, WINDOWS_CLOSE, WINDOWS_BIG,
        SOUND_BEEP, SEARCH, OPEN_FILE
    }

    public String sessionId;

    public Type type;
    public List<MVector<String, String>> args;

}
