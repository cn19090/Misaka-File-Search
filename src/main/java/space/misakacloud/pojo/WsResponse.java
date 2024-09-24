package space.misakacloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsResponse<T> {
    public enum Type {
        SEARCH_RESULT, THUMBNAIL
    }
    private String sessionId;

    private Type type;
    private T contains;
}
