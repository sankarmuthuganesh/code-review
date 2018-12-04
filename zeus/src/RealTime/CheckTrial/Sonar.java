//package RealTime.CheckTrial;
//
//import org.eclipse.jgit.transport.CredentialsProvider;
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
//import org.gitlab4j.api.GitLabApi;
//import org.gitlab4j.api.GitLabApiException;
//import org.gitlab4j.api.ProjectApi;
//
//
//
//public class Sonar {
//  public static void main(String[] args) throws GitLabApiException { 
//    CredentialsProvider user = new UsernamePasswordCredentialsProvider("sundaravelvar", "Sundar989793");
//    GitLabApi kk=GitLabApi.login("http://192.168.41.136/", "sankraja", "ivtl@2018");
//    ProjectApi pro = kk.getProjectApi();
//    pro.getAllProjects().forEach(l ->{
//      System.out.println(l.getName());
//    });
//    
//  }
//}
