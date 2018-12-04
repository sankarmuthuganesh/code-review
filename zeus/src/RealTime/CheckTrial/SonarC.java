package RealTime.CheckTrial;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.sonarqube.ws.Issues.Issue;
import org.sonarqube.ws.WsCe.ActivityResponse;
import org.sonarqube.ws.WsCe.Task;
import org.sonarqube.ws.WsMeasures.ComponentWsResponse;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.HttpException;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.ce.ActivityWsRequest;
import org.sonarqube.ws.client.issue.SearchWsRequest;
import org.sonarqube.ws.client.measure.ComponentWsRequest;

public class SonarC {
public static void main(String[] args) throws ParseException {
String branch="river-1803";
String repository="hue-scm-sales";
    HttpConnector httpConnector;
    String componentKey;
    String urlLink;
    if (branch.startsWith("river-")) {
        urlLink = "http://192.168.41.209:8090/";
        httpConnector = HttpConnector.newBuilder().url(urlLink)
                .credentials("sundaravel", "sundaravel.v@ivtlinfoview.co.jp").build();
        componentKey = "com.worksap.company:" + repository + ":" + branch;
    } else {
        String repo;
        urlLink = "http://192.168.41.233/";
        httpConnector = HttpConnector.newBuilder().url(urlLink)
                .credentials("hari", "abcd1234").build();
        repo = repository + ":" + repository;
        branch = branch + "/" + "";
        ;
        componentKey = "com.worksap.company:" + repo + "/" + branch;
    }

    WsClient wsClient = WsClientFactories.getDefault().newClient(httpConnector);
    // ComponentRequest
    ComponentWsRequest componentWsRequest = new ComponentWsRequest();
    componentWsRequest.setComponentKey(componentKey);
    componentWsRequest.setMetricKeys(Arrays.asList("bugs", "vulnerabilities", "code_smells"));

    Map<String, String> totalIssuesAndRunTime = new HashMap<>();
    ComponentWsResponse componentWsResponse = null;
    try {
        componentWsResponse = wsClient.measures().component(
                componentWsRequest);
    } catch (HttpException e) {
        // Cannot fetch SonarRun Time
        totalIssuesAndRunTime.put("runtime", "!");
        totalIssuesAndRunTime.put("totalNumberOfIssues", "0");
    }

    // ISSUES

    SearchWsRequest issues = new SearchWsRequest();
    List<String> componentKeys = Arrays.asList(componentKey);
    List<String> bugTypes = Arrays.asList("BUG", "VULNERABILITY", "CODE_SMELL");
    List<String> severities = Arrays.asList("BLOCKER", "MAJOR", "MINOR", "CRITICAL", "INFO");
    issues.setComponentKeys(componentKeys);
    issues.setTypes(bugTypes);
    issues.setStatuses(Arrays.asList("OPEN"));
    issues.setSeverities(severities);
    int pageCount = (int)(wsClient.issues().search(issues).getTotal() / 100 + 1);
    List<Issue> totalIssueList = new ArrayList<>();
    for (int iterator = 1; iterator <= pageCount; iterator++) {
        issues.setPage(iterator);
        totalIssueList.addAll(wsClient.issues().search(issues).getIssuesList());
    }

    List<Issue> issuesOfEpic = totalIssueList.stream()
            .collect(Collectors.toList());

    // ActivityRequest
    ActivityWsRequest activityRequest = new ActivityWsRequest();
    activityRequest.setComponentId(componentWsResponse.getComponent().getId());
    ActivityResponse activityResponse = wsClient.ce().activity(activityRequest);
    List<Task> taskList = activityResponse.getTasksList();
    Optional<Task> executedDate = taskList.stream().collect(
            Collectors.maxBy(Comparator.comparing(Task::getExecutedAt)));
    String dateFromSonar = "";
    if (executedDate.isPresent()) {
        dateFromSonar = executedDate.get().getExecutedAt();
    } else {
        totalIssuesAndRunTime.put("runtime", "!");
        totalIssuesAndRunTime.put("totalNumberOfIssues", String.valueOf(totalIssueList.size()));
    }
    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    simpleFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
    Date form;
   
        form = simpleFormat.parse(dateFromSonar);
//        form.setHours(form.getHours()-3);
        form.setMinutes(form.getMinutes()-4);
        form.setSeconds(form.getSeconds()+7);
    DateFormat formatter = DateFormat.getDateTimeInstance();
    String afdsf = formatter.format(form);
  System.out.println(formatter.format(form));


}
}
