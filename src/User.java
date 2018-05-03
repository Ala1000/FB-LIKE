import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        this.setUsername(username);
        this.password = password;
        prepareUserDir();
        saveUserInfo();
    }

    private void prepareUserDir() {
        File newDir = new File(personalFilesLoc+ getUsername());
        if(newDir.mkdir()){
            File[] files = {new File(personalFilesLoc+ getUsername() +"/friends.txt"),new File(personalFilesLoc+ getUsername() +"/info.txt"),new File(personalFilesLoc+ getUsername() +"/posts.txt"),new File(personalFilesLoc+ getUsername() +"/requests.txt")};
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
        this.setUsername(username);
    }

    private void saveUserInfo() {
        try {
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+ getUsername() +"/info.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append(fullname+"$"+dob+"$"+livingLoc+"$"+eduInfo+"$"+ getUsername() +"$"+password);
            bw.newLine();
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
            bw.append("\n"+ this.getUsername() +"$"+this.password);
            bw.newLine();
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
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+friendName+"/requests.txt",true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append(this.getUsername() +" Wants to add you to their friend list\n");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllRequests(){
        List<String> results = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(personalFilesLoc+ getUsername() +"/requests.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                results.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void acceptRequest(String friend){
        String myFriendsListPath = personalFilesLoc+ getUsername() +"/friends.txt";
        String otherFriendsListPath = personalFilesLoc+friend+"/friends.txt";
        try {
            FileOutputStream fos1 = new FileOutputStream(myFriendsListPath,true);
            FileOutputStream fos2 = new FileOutputStream(otherFriendsListPath,true);
            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));
            BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));
            bw1.newLine();
            bw2.newLine();
            bw1.write(friend);
            bw2.write(getUsername());
            bw1.flush();bw2.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emptyFile() {
        try {
            PrintWriter pw = new PrintWriter(personalFilesLoc+ getUsername() +"/requests.txt");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addPost(String post){
        try {
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+ getUsername() +"/posts.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.append(post);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> listFriends(){
        List<String>friends = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(personalFilesLoc+ getUsername() +"/friends.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()){
                    friends.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public void sendMessage(String recipient, String message){
        try{
            String sender = Integer.parseInt(getUsername().substring(4))<Integer.parseInt(recipient.substring(4))? getUsername() :recipient;
            String reciver = Integer.parseInt(getUsername().substring(4))>Integer.parseInt(recipient.substring(4))? getUsername() :recipient;
            String filename = sender+"-"+reciver+"-chat.txt";
            File myMessage = new File(personalFilesLoc+ getUsername() +"/"+filename);
            File recipientMessage = new File (personalFilesLoc+recipient+"/"+filename);
            if (!myMessage.exists()){
                myMessage.createNewFile();
            }
            if(!recipientMessage.exists()){
                recipientMessage.createNewFile();
            }
            FileOutputStream fos1 = new FileOutputStream(myMessage,true);
            FileOutputStream fos2 = new FileOutputStream(recipientMessage,true);
            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));
            BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));
            bw1.append(getUsername() +":");
            bw1.newLine();
            bw1.append(message);
            bw2.append(getUsername() +":");
            bw2.newLine();
            bw2.append(message);
            bw1.newLine();bw2.newLine();
            bw1.flush();bw2.flush();
            bw1.close();bw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getAllPosts(){
        ArrayList<String> posts = new ArrayList<>();
        try{
            String myPostsLoc = personalFilesLoc+ getUsername() +"/posts.txt";
            FileInputStream fis = new FileInputStream(myPostsLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()){
                    posts.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }
    public static List<String> getAllUserPosts(String username){
        ArrayList<String> posts = new ArrayList<>();
        try{
            String myPostsLoc = personalFilesLoc+ username +"/posts.txt";
            FileInputStream fis = new FileInputStream(myPostsLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()){
                    posts.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
