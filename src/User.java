import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 4/28/2018.
 * The class represents a template for users information and actions.
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

    public User(String username){
        this.setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    /**
    *This method create a folder for each registered user. The folder (at creation) contains the following files:
     * info.txt: It contains user info.
     * friends.txt: List of user friends
     * posts.txt: contains user's posts.
     * requests.txt: Requests that the use need to respond to.
    */
    private void prepareUserDir() {
        File newDir = new File(personalFilesLoc+ getUsername());
        //Create the folder, and check if it was created successfully.
        if(newDir.mkdir()){
            //List of files that need to be included in the new users folder
            File[] files = {new File(personalFilesLoc+ getUsername() +"/friends.txt"),new File(personalFilesLoc+ getUsername() +"/info.txt"),new File(personalFilesLoc+ getUsername() +"/posts.txt"),new File(personalFilesLoc+ getUsername() +"/requests.txt")};
            for (File file:files) {
                try {
                    //Create the files
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Save user's information in info.txt
    private void saveUserInfo() {
        try {
            FileOutputStream fos = new FileOutputStream(personalFilesLoc+ getUsername() +"/info.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            //Separate fields by "$"
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

    /**
     * This method adds the user to Users.txt, which contains all registered users.
     * */
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

    /**
     * @param friendName
     * The method takes username for the user to be added as friend, and adds it to the other user's requests.txt file
     */
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

    /**
     * This method returns all request in requests.txt.
     */
    public List<String> getAllRequests(){
        List<String> results = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(personalFilesLoc+ getUsername() +"/requests.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            //Read all requests
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

    /**
     * @param friend
     * This method accepts a friendship request by adding current user to friend's "friends.txt file,
     * And friend to current user's "friends.txt"
     **/
    public void acceptRequest(String friend){
        //My friends.txt
        String myFriendsListPath = personalFilesLoc+ getUsername() +"/friends.txt";
        //My friend's friends.txt
        String otherFriendsListPath = personalFilesLoc+friend+"/friends.txt";
        try {
            //Add to each other file.
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

    /**
     *Clear the requests.txt file once the user have responded to all requests
     */
    public void emptyFile() {
        try {
            PrintWriter pw = new PrintWriter(personalFilesLoc+ getUsername() +"/requests.txt");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param post
     * This method accepts a post as String and adds it to posts.txt file.
     */
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

    /**
     * This method returns a list of all users in my friends.txt file.
     */
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

    /**
     * @param recipient
     * @param message
     * This method takes recipient and message as parameters
     * The recipient is the user who will receive the message
     * The message is the contents that will be sent to the recipient
     */
    public void sendMessage(String recipient, String message){
        try{
            //To avoid the case where two files are created for messages between the same users, the chat file's name will have the user with lower ID first
            String sender = Integer.parseInt(getUsername().substring(4))<Integer.parseInt(recipient.substring(4))? getUsername() :recipient;
            String reciver = Integer.parseInt(getUsername().substring(4))>Integer.parseInt(recipient.substring(4))? getUsername() :recipient;
            String filename = sender+"-"+reciver+"-chat.txt";
            //Add the file to both user's folders
            File myMessage = new File(personalFilesLoc+ getUsername() +"/"+filename);
            File recipientMessage = new File (personalFilesLoc+recipient+"/"+filename);
            //Create the files if they don't exit already
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

    /**
     * Get All posts in the user's posts.txt
     * @return posts
     */
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

    /**
     * @param username
     * @return List of posts for a current user
     */
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

    /**
     * This method returns the last message between this user and other users
     * @return List of chatting files and the last message between current user and other users.
     */
    public HashMap<File,String> getAllMessages(){
        HashMap<File,String> friendsMessages = new HashMap<>();
        //Get all friends
        List<String> friends = listFriends();
        File myFolder = new File(personalFilesLoc+username);
        File[] listOfFiles = myFolder.listFiles();
        //For each file in my directory, if the file name contains "chat", add the file to the hashmap with the last line in it.
        for (File file : listOfFiles){
            if(file.isFile()){
                if (file.getName().contains("chat")){
                    String lastMessage = getLastMessage(file);
                    friendsMessages.put(file,lastMessage);
                }
            }
        }
        return friendsMessages;
    }

    /**
     * @param file as a chatting filr between current user and another one
     * @return lastLine as the last message in the chatting file
     */
    private String getLastMessage(File file) {
        String lastLine = "";
        try {
            String sCurrentLine;
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br= new BufferedReader(new InputStreamReader(fis));
            while ((sCurrentLine = br.readLine()) != null)
            {
                lastLine = sCurrentLine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastLine;
    }

    /**
     * @param otherUser
     * @return text as the full conversation between this user and otherUser
     */
    public String displayChat(String otherUser){
        String text="";
        String filePath = personalFilesLoc+username+"/";
        try {
            //File name depends on which user has lower number
            filePath += Integer.parseInt(getUsername().substring(4))<Integer.parseInt(otherUser.substring(4))? getUsername()+"-"+otherUser+"-chat.txt" : otherUser+"-"+getUsername()+"-chat.txt";
            text = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * @param username for the user whom we need his info
     * @return result as user's info
     */
    public static String getUserInfo(String username){
        String result="";
        try{
            String userFile = personalFilesLoc+username+"/info.txt";
            String[] userData;
            FileInputStream fis = new FileInputStream(userFile);
            BufferedReader bw = new BufferedReader(new InputStreamReader(fis));
            String data = bw.readLine();
            userData = data.split("\\$");
            result = "User "+username+" was found\nAdditional info about the user: \n";
            result += "BirthDate: "+userData[1]+"\n"+"Living location: "+userData[2]+"\n"+"Eduction Info: "+userData[3];
         } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
