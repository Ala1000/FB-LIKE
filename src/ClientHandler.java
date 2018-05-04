import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by User on 4/28/2018.
 * The class is responsible for sending data/instructions and accepting responses from concurrent users
 */
public class ClientHandler extends Thread{
    //This file contains all registered users.
    final static String usersFileLoc = "Users.txt";
    //This folder contains users folders
    final static String personalFilesLoc = "UsersData/";
    //Birthday format for user
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    //Allowed options for logged in users
    String optionsAllowed = "AddFriend\nAddPost\nListFriends\nSendMessages\nShowWall\nRespondToRequest\nShowInbox\nSearch\nLogOff";
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    User currentUser = null;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        //this variable contains the client's responce
        String received;
        try {
            // Ask user what he wants
            dos.writeUTF("Please choose the required action..\n" +
                    "Type Exit to terminate connection.");
            while (true) {
                // receive the answer from client
                received = dis.readUTF();

                if (received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }
                // write on output stream based on the
                // answer from the client
                switch (received) {
                    case "SignUp":
                        //Fill in new user's info
                        dos.writeUTF("Enter your full name");
                        String name = dis.readUTF();
                        dos.writeUTF("Enter your birth date in dd/MM/yyyy format");
                        Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(dis.readUTF());
                        dos.writeUTF("Enter your living location");
                        String loc = dis.readUTF();
                        dos.writeUTF("Enter your education info");
                        String edu = dis.readUTF();
                        dos.writeUTF("Enter your username");
                        String username = dis.readUTF();
                        String pass, passConfirm;
                        dos.writeUTF("Enter your Password");
                        pass = dis.readUTF();
                        dos.writeUTF("Enter your Password confirmation");
                        passConfirm = dis.readUTF();
                        while (!pass.contains(passConfirm)) {
                            dos.writeUTF("Password and Password-Confirmation need to be similar\nPlease retry\n");
                            pass = dis.readUTF();
                            dos.writeUTF("Enter your Password confirmation");
                            passConfirm = dis.readUTF();
                        }
                        User user = new User(name, dob, loc, edu, username, pass);
                        user.saveUser();
                        dos.writeUTF("You have registered successfully ^_^.. \n Please Sign in to continue ..");
                        break;

                    case "SignIn":
                        dos.writeUTF("Enter your username");
                        String user_name = dis.readUTF();
                        dos.writeUTF("Enter your password");
                        String password = dis.readUTF();
                        //Check Users.txt for a user with the received username and password
                        while (!checkUserExists(user_name, password)) {
                            dos.writeUTF("Either username or password is wrong, Please try again\nEnter your username");
                            user_name = dis.readUTF();
                            dos.writeUTF("Enter your password");
                            password = dis.readUTF();
                        }
                        String userData = readUserInfo(user_name);
                        currentUser = new User(user_name);
                        dos.writeUTF("You are now signed in :)\n" + "Yor info:\n" + userData + "\nActions Allowed\n" + optionsAllowed + "\n\n" + "Choose your next action");
                        break;

                    case "Add_Friend":
                        if (currentUser != null) {
                            dos.writeUTF("Enter friend name");
                            String friendToAdd = dis.readUTF();
                            if (checkUserExists(friendToAdd)) {
                                //If friend exists, add him/her
                                currentUser.sendFriendRequest(friendToAdd);
                                dos.writeUTF("Friend request was sent successfully\n Please choose your next action");
                            }
                            else {
                                dos.writeUTF("No user with such name exists, Please try again or choose another action");
                            }
                        } else {
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;

                    case "Check_Requests":
                        if (currentUser != null) {
                            List<String> requests = currentUser.getAllRequests();
                            int endIndex;
                            for (String request:
                                requests ) {
                                //Display the request for the client with Accept|Decline
                                endIndex = request.indexOf(" Wants");
                                String otherUser = request.substring(0,endIndex);
                                dos.writeUTF(request+"    Accept|Decline");
                                String reply = dis.readUTF();
                                if (reply.contains("Accept")){
                                    currentUser.acceptRequest(otherUser);
                                }
                            }
                            //After client responded to all requests, clean requests.txt
                            currentUser.emptyFile();
                            dos.writeUTF("Yo have no requests left. Please choose your next action");
                        } else {
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "Add_Post":
                        if (currentUser != null){
                            dos.writeUTF("Please Add a post");
                            String post = dis.readUTF();
                            currentUser.addPost(post);
                            dos.writeUTF("Post was added successfully\nPlease choose your next action");
                        }
                        else {
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "List_Friends":
                        if (currentUser!= null){
                            List<String> friends = currentUser.listFriends();
                            String results = "";
                            //Display all friends
                            for(String friend:
                                    friends){
                                results+= friend+"\n";
                            }
                            dos.writeUTF(results+"Please Choose your next action");
                        }
                        else{
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "Send_Message":
                        if (currentUser!= null){
                            dos.writeUTF("Enter the recipient name");
                            String recipient = dis.readUTF();
                            dos.writeUTF("Enter your message");
                            String message = dis.readUTF();
                            currentUser.sendMessage(recipient,message);
                            dos.writeUTF("Message was sent successfully\n Please choose your next action");
                        }

                        else{
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "Show_Wall":
                        if (currentUser!= null){
                            HashMap<String,ArrayList<String>> usersPosts = new HashMap<>();
                            List<String> posts;
                            //Get posts for current user
                            posts = currentUser.getAllPosts();
                            usersPosts.put(currentUser.getUsername(),new ArrayList<>());
                            for (String post: posts){
                                usersPosts.get(currentUser.getUsername()).add(post);
                            }
                            //Get All friend's posts
                            List<String> friends = currentUser.listFriends();
                            for(String friend: friends){
                                usersPosts.put(friend, new ArrayList<>());
                                posts = User.getAllUserPosts(friend);
                                for (String post: posts){
                                    usersPosts.get(friend).add(post);
                                }
                            }
                            String result = "";
                            //Iterate on posts and display them
                            Iterator it = usersPosts.entrySet().iterator();
                            while (it.hasNext()){
                                Map.Entry pair = (Map.Entry)it.next();
                                posts = (ArrayList<String>)pair.getValue();
                                result+= pair.getKey()+" posts:\n*******************************************************\n";
                                for (String post:posts){
                                    result+= post+"\n";
                                }
                                it.remove();
                            }
                            dos.writeUTF(result+"\n"+"Please choose your next action");
                        }

                        else{
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "Show_Inbox":
                        if (currentUser != null){
                            //Show all messages for current user
                            HashMap<File,String> allMeesages = currentUser.getAllMessages();
                            String result = "Your Inbox \n *****************************************************\n";
                            Iterator it = allMeesages.entrySet().iterator();
                            while(it.hasNext()){
                                Map.Entry pair = (Map.Entry)it.next();
                                String message = (String)pair.getValue();
                                String[] chatUsers = ((File)pair.getKey()).getName().split("-");
                                String otherUser = chatUsers[0].contains(currentUser.getUsername())?chatUsers[1]:chatUsers[0];
                                result+= otherUser+" :\n"+message;
                            }

                            dos.writeUTF(result+"\nChoose a user to display your conversations\n");
                            String otherUser = dis.readUTF();
                            String chat = currentUser.displayChat(otherUser);
                            dos.writeUTF(chat+"\nPlease choose your next action");
                        }
                        else {
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "Search":
                        //Search for a user
                        if (currentUser!= null){
                            dos.writeUTF("Enter username to search for");
                            String userToSearchFor = dis.readUTF();
                            if(checkUserExists(userToSearchFor)){
                                dos.writeUTF(User.getUserInfo(userToSearchFor)+"\n"+"Please choose your next action");
                            }
                            else{
                                dos.writeUTF("User not found\nPlease choose your next action");
                            }
                        }

                        else{
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    case "LogOff":
                        if (currentUser!= null){
                            dos.writeUTF("You have been logged of successfully\nPlease SignIn or SignUp to start a new action");
                            currentUser=null;
                        }

                        else{
                            dos.writeUTF("Please Sign in first\nPlease choose SignIn or SignUp first.");
                        }
                        break;
                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }
            }


            try {
                // closing resources
                this.dis.close();
                this.dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean checkUserExists(String user_name, String password) {
        try {
            FileInputStream fis = new FileInputStream(usersFileLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                String[] userdata = line.split("\\$");
                if (userdata[0].contains(user_name) && userdata[1].contains(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkUserExists(String user_name) {
        try {
            FileInputStream fis = new FileInputStream(usersFileLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for (String line; (line = br.readLine()) != null; ) {
                if (line.contains(user_name)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String readUserInfo(String username) {
        String fielLoc = "UsersData/" + username + "/Info.txt";
        try {
            FileInputStream fis = new FileInputStream(fielLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String data = br.readLine();
            data = data.replace('$', '\n');
            return data;
        } catch (FileNotFoundException e) {
            return "Not found";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
