package RealTime.GroupingBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Entity.BugDetails;
import RealTime.Entity.FileUnit;

public class GroupingByy {
	
	//Repository - Branched - FileDetails.
	Map<String, Map<MultiKey, List<FileUnit>>> optimusBugs;
	
	//The Order Of Grouping.
	List<String> order=new ArrayList<>();
	
	public Map<String, Map<MultiKey, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>>>> entryPoint(Map<String, Map<MultiKey, List<FileUnit>>> optimusBugs){
		this.optimusBugs=optimusBugs;

		Map<String,Map<MultiKey, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>>>> fullOut=new HashMap<>();
		
		//GroupBy Author
		optimusBugs.entrySet().stream().forEach(repository -> {
			Map<MultiKey, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>>> branchesBugs=new HashMap<>();
			repository.getValue().entrySet().forEach(branch ->{
				branchesBugs.put(branch.getKey(), groupByAuthor(branch.getValue()));
			});
			fullOut.put(repository.getKey(), branchesBugs);
		});
		
		return fullOut;
	}

	public Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>> groupByAuthor(List<FileUnit> files){

		//The Order - NatureOfFile, @AuthorOfFile, Subsystem, Epic, SeverityOfBug, BugCategory.
		Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>> out = files.stream().collect(
				Collectors.groupingBy(FileUnit::getNatureOfFile,
						Collectors.groupingBy(FileUnit::getAuthorOfFile,
								Collectors.groupingBy(FileUnit::getSubSystem,
										Collectors.groupingBy(FileUnit::getEpicName,GroupingByy.flatMapping(fileUnit -> fileUnit.getBugDetailsList().stream(), 
												Collectors.groupingBy(BugDetails::getSeverityOfBug,
														Collectors.groupingBy(BugDetails::getBugCategory))))))));

		return out;
	}
	public void GroupByCategory(List<FileUnit> files){

	}

	public void GroupByEpic(){

	}
	public void GroupBySubsystem(){

	}
	public void GroupByLicense(){

	}
	public void groupByLicenseGroup(){

	}
	//This is going to be more useful from developer point of view.
	public void groupByFileName(List<FileUnit> files){

		//The Order - NatureOfFile, @AuthorOfFile, Subsystem, Epic, SeverityOfBug, BugCategory.
		Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>> out = files.stream().collect(
				Collectors.groupingBy(FileUnit::getNatureOfFile,
						Collectors.groupingBy(FileUnit::getFileName,
								GroupingByy.flatMapping(fileUnit -> fileUnit.getBugDetailsList().stream(), 
												Collectors.groupingBy(BugDetails::getSeverityOfBug,
														Collectors.groupingBy(BugDetails::getBugCategory))))));
	}
	//Java 9 - Collector.flatMapping
	public static <T,U,A,R> Collector<T,?,R> flatMapping(Function <? super T, ? extends Stream < ? extends U>> mapper, 
			Collector<? super U,A,R> downstream){
		BiConsumer<A,? super U> acc=downstream.accumulator();
		return Collector.of(downstream.supplier(), (a,t) -> {
			try(Stream<? extends U> s=mapper.apply(t)){
				if(s!=null)s.forEachOrdered(u -> acc.accept(a,u));
			}
		}, downstream.combiner(), downstream.finisher(),downstream.characteristics().stream().toArray(Collector.Characteristics[]::new));
	}
}
