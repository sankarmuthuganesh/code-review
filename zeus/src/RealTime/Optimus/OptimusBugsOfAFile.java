package RealTime.Optimus;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OptimusBugsOfAFile {
	List<String> errors ;
	List<String> warnings ;
	
	public OptimusBugsOfAFile(){
		this.errors=new ArrayList<>();
		this.warnings=new ArrayList<>();
	}
}
