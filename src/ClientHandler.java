import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 4/28/2018.
 */
public class ClientHandler extends Thread{
    final static String usersFileLoc = "Users.txt";
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {
        String received;
        String toreturn;
        while (true)
        {
            try {

                // Ask user what he wants
                dos.writeUTF("Please choose the required action..\n"+
                        "Type Exit to terminate connection.");

                // receive the answer from client
                received = dis.readUTF();

                if(received.equals("Exit"))
                {
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

                    case "SignUp" :
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
                        while(!pass.contains(passConfirm)){
                            dos.writeUTF("Password and Password-Confirmation need to be similar\nPlease retry\n");
                            pass = dis.readUTF();
                            dos.writeUTF("Enter your Password confirmation");
                            passConfirm = dis.readUTF();
                        }
                        User user = new User(name,dob,loc,edu,username,pass);
                        user.saveUser();
                        break;

                    case "SignIn" :
                        dos.writeUTF("Enter your username");
                        String user_name = dis.readUTF();
                        dos.writeUTF("Enter your password");
                        String password = dis.readUTF();
                        while(!checkUserExists(user_name,password)){
                            dos.writeUTF("Either username or password is wrong, Please try again\nEnter your username");
                            user_name = dis.readUTF();
                            dos.writeUTF("Enter your password");
                            password = dis.readUTF();
                        }
                        dos.writeUTF("You are now signed in :)");
                        break;

                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean checkUserExists(String user_name, String password) {
        try {
            FileInputStream fis = new FileInputStream(usersFileLoc);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            for(String line; (line = br.readLine()) != null; ){
                    String[] userdata = line.split("\\$");
                    if (userdata[0].contains(user_name) && userdata[1].contains(password)){
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
}
