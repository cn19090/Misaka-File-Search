package space.misakacloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class TreeNode <T> implements Serializable {
    private T data;
    private LinkedList<TreeNode> next;

}
