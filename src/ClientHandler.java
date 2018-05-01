import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 4/28/2018.
 */
public class ClientHandler extends Thread{
    final static String usersFileLoc = "Users.txt";
    final static String personalFilesLoc = "UsersData/";
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
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
        String received;
        String toreturn;
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

                // creating Date object
                Date date = new Date();

                // write on output stream based on the
                // answer from the client
                switch (received) {

                    case "SignUp":
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
                                endIndex = request.indexOf(" Wants");
                                String otherUser = request.substring(0,endIndex);
                                dos.writeUTF(request+"    Accept|Decline");
                                String reply = dis.readUTF();
                                if (reply.contains("Accept")){
                                    currentUser.acceptRequest(otherUser);
                                }
                            }
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
