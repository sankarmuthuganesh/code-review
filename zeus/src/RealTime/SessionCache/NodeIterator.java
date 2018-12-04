package RealTime.SessionCache;



import java.io.IOException;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;


public class NodeIterator {
	public interface NodeHandler{
		boolean handle(Node node) throws ParseException, IOException ;
	}
	private NodeHandler nodeHandler;
	public NodeIterator(NodeHandler nodeHandler){
		this.nodeHandler=nodeHandler;
	}
	public void  explore(Node node) throws ParseException, IOException {
		if(nodeHandler.handle(node)){
			for(Node child:node.getChildNodes()){
				explore(child);
			}
		}	
	}
}
