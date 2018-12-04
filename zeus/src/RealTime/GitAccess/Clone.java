package RealTime.GitAccess;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;

import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Clone
{
    private static final HttpTransport transport = new NetHttpTransport();
    public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException
    {
//        String userPrivateToken = "ohLKos1F6wyf1yPr81iy";
	String gitConnection= "http://" + "192.168.41.190" + "/root/";
//        String gitConnection = "http://gitlab-ci-token:" + "PJbdcSqGA8-y_no6vfxh" + "@192.168.41.190/root/";
//        List<String> branches = new ArrayList<>();
//        Collection<Ref> call;
//            call = Git
//                    .lsRemoteRepository()
//                    .setHeads(true)
//                    .setCredentialsProvider(
//                            new UsernamePasswordCredentialsProvider("karthikrajasan", "Ivtl@2015"))
//                    .setRemote(gitConnection + "hue-tracking-develop" + ".git").call();
//            for (Ref ref : call) {
//                branches.add(ref.getName());
//            }
//        System.out.println(branches);
//String gitConnection= "http://" + "192.168.40.227" + "/root/";
//        Git gitBranch =
//            Git.cloneRepository().setURI(gitConnection + "hue-tracking-develop.git").setBranch("codereview-gravity")
//                .setDirectory(new File("D:\\Sankard\\"))
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("karthikrajasan", "Ivtl@2015"))
//                .call();
        getProjects();

        // https://github.com/deeplearning4j/deeplearning4j
        // http://192.168.41.136/root/hue-hr-commutingal.git
    }

    private static void getProjects()
    {
        String gitConnectionURL = "http://" + "192.168.41.136" + "/";
//        CredentialsProvider userIdentity = new UsernamePasswordCredentialsProvider("karthikrajasan", "Ivtl@2015");
        GitlabAPI gitLabApi = null;
        try {
//            Map<TokenType, String> typeandToken = obtainAccessToken(gitConnectionURL, "karthikrajasan", "Ivtl@2015", false);
//            Entry<TokenType, String> entry = typeandToken.entrySet().iterator().next();
            gitLabApi = GitlabAPI.connect(gitConnectionURL, "jJakGujxcJv6yCvR_xi8", TokenType.PRIVATE_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> listofRepositories = new ArrayList<>();
        try {
            List<GitlabProject> projects;
            projects = gitLabApi.getProjects();
            for (GitlabProject project : projects) {
                listofRepositories.add(project.getName());
            }
        } catch (IOException e) {
            // Cannot get List of Repositories. And Hence Number of Repositories will be 0.!
        }
        System.out.println(listofRepositories);
    }

    public static Map<TokenType, String> obtainAccessToken(String gitlabUrl, String username, String password,
        boolean sudoScope) throws IOException
    {
        Map<TokenType, String> tokenTypeAndToken = new HashMap<>();
        try {
            final OAuthGetAccessToken tokenServerUrl =
                new OAuthGetAccessToken(gitlabUrl + "/oauth/token" + (sudoScope ? "?scope=api%20sudo" : ""));
            final TokenResponse oauthResponse =
                new PasswordTokenRequest(transport, JacksonFactory.getDefaultInstance(), tokenServerUrl, username,
                    password).execute();
            tokenTypeAndToken.put(TokenType.ACCESS_TOKEN, oauthResponse.getAccessToken());
            return tokenTypeAndToken;
        } catch (TokenResponseException e) {
            if (sudoScope && e.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Fallback for pre-10.2 gitlab versions
                final GitlabSession session = GitlabAPI.connect(gitlabUrl, username, password);
                tokenTypeAndToken.put(TokenType.PRIVATE_TOKEN, session.getPrivateToken());
                return tokenTypeAndToken;
            } else {
                throw new GitlabAPIException(e.getMessage(), e.getStatusCode(), e);
            }
        }
    }

}
