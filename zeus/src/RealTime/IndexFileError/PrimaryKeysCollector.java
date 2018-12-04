package RealTime.IndexFileError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryKeysCollector {
	 private Map<String,Integer> primaryKeysAndOrder;
	private Map<String,List<String>> primarykeys;
}
