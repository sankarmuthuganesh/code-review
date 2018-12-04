package RealTime.CheckTrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Sim {

	public static void main(String[] args) {
		Sam kk=new Sam();
		kk.get();

//List<String> rdbReservedKeywordsList =
//			Arrays.asList("from", "and", "or", "alter", "table", "as", "between",
//					"where", "update", "union", "all", "truncate", "top", "select",
//					"into", "distinct", "select", "like", "order by", "full join",
//					"right join", "inner join", "left join", "insert into", "in",
//					"create database", "create table", "create index", "create view",
//					"drop database", "drop index", "delete", "drop table", "exists",
//					"group by", "having");
//
//List<String> usedKeywordsList = new ArrayList<>();
//		
//		String query="SELECT * FROM pj_jv_expense_adjust_application_trn_where AS " +
//	            "a INNER JOIN pj_jv_balance_trn AS b on a.pj_jv_balance_trn_id = b.pj_jv_balance_trn_id " +
//	            "where application_in = ?";
//		
//		rdbReservedKeywordsList.stream().forEach(keyword ->{
//			Pattern regex=Pattern.compile("(?i)"+keyword);
//			Matcher match = regex.matcher(query);
//			if(match.find()){
//				usedKeywordsList.add(match.group());
//			}
//		});
//		System.out.println(usedKeywordsList);
//		
//		usedKeywordsList.stream().forEach(used ->{
//		if(!StringUtils.isAllUpperCase(used.replaceAll(" ",""))){
//			System.out.println("\""+used+"\" should be in upper case in the query -> "+ query);
//		}
//		});
//
//	}
	}
}
