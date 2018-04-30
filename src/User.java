import java.io.*;
import java.util.Date;

/**
 * Created by User on 4/28/2018.
 */
public class User implements Serializable{

    final static String usersFileLoc = "Users.txt";
    final static String personalFilesLoc = "UsersData/";
    private String fullname;
    private Date dob;
    private String livingLoc;
    private String eduInfo;
    private String username;
    private String password;

    public User(String fullname, Date dob, String livingLoc, String eduInfo, String username, String password) {
        this.fullname = fullname;
        this.dob = dob;
        this.livingLoc = livingLoc;
        this.eduInfo = eduInfo;
        this.username = username;
        this.password = password;
        prepareUserDir();
        saveUserInfo();
    }

    private void prepareUserDir() {
        File newDir = new File(personalFilesLoc+username);
        if(newDir.mkdir()){
            File[] files = {new File(personalFilesLoc+username+"/friends.txt"),new File(personalFilesLoc+username+"/info.txt"),new File(personalFilesLoc+username+"/posts.txt"),new File(personalFilesLoc+username+"/requests.txt")};
            for (File file:files) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public User(String username){
        this.username = username;
    }

    private void saveUserInfo() {
        try {
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+username+"/info.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append(fullname+"$"+dob+"$"+livingLoc+"$"+eduInfo+"$"+username+"$"+password);
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUser()  {
        try {
            FileOutputStream fos = new FileOutputStream(usersFileLoc,true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append("\n"+this.username+"$"+this.password);
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendRequest(String friendName){
        try {
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+friendName+"/requests.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append("\n"+this.username+" Wants to add you to their friend list");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
