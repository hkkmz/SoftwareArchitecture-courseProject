// Hatice Kubra Korkmaz
// A Pluggable Authentication Mechanism
// Patterns that are used: Singleton, Iterator, Command, Template, Observer

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Singleton Pattern is used here.
class OperatingSystem{
    private static OperatingSystem os =null;
    private static final Lock lock = new ReentrantLock();

    public static OperatingSystem getInstance(){
        if(os == null){
            lock.lock();
            try {
                if (os == null)
                    os = new OperatingSystem();
            } finally {
                lock.unlock();
            }
        }return os;
    }
    //Constructor
    private OperatingSystem(){}
    AuthenticationMechanism authenticationMechanism;
}

interface Command{
    public void execute();
}
class Invoker{
    private List<Command> commandList = new ArrayList<Command>();

    public void takeCommend(Command command){
        commandList.add(command);
    }

    public void placeCommand(){

        for (Command command : commandList) {
            command.execute();
        }
        commandList.clear();
    }
}

// 'Observer' class of the Observer pattern.
interface Observer {
    public void UserAuthenticated(AuthenticationMechanism authMech);
}
// 'ConcreteObserver' class of Observer pattern.
class User implements Observer {
    private String username;
    private int id;
    private String password;
    private String authMechName; // Internal Observer state
    private AuthenticationMechanism authenticationMechanism;

    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username;}

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }

    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }

    public String getAuthMechName(){ return authMechName; }
    public void setAuthMechName(){ this.authMechName = authMechName; }

    public User(String username, int id, String password){
        this.setUsername(username);
        this.setId(id);
        this.setPassword(password);
    }
    // method of Observer pattern.
    @Override
    public void UserAuthenticated(AuthenticationMechanism authMech) {
        authenticationMechanism = authMech;
        authMechName = authMech.getAuthMechName();
        System.out.println(username + " authenticated to system newly!");
    }
    ArrayList<User> users = new ArrayList<User>();
    public void check(){
        System.out.println("Checking user "+username);
        for(int i=0;i<users.size();i++){
            if(users.get(i).username == username){
                System.out.println("User is found!");
            }else
                System.out.println("There is no such user, try again later...");
        }

    }
}

// This is the 'abstract' class of Template Method.
// Also 'Subject' class of Observer pattern.
abstract class AuthenticationMechanism  implements Command{
    User user;

    protected String authMechName;

    public String getAuthMechName(){ return authMechName; }
    public void setAuthMechName(String local){ this.authMechName = authMechName; }

    public AuthenticationMechanism(){

    }

    protected abstract int authenticate(String username, String password);
    ArrayList<User> users = new ArrayList<User>();

    // component of Observer
    public void Attach (User user) {
        users.add(user);
    }
    // component of Observer
    public void Detach (User user) {
        for (int i = 0; i< users.size(); i++) {
            if (users.get(i).getUsername() == user.getUsername()) {
                users.remove(i);
                return;
            }
        }
    }
    // component of Observer
    public void Notify() {
        //Tell the user that he/she is authenticated.
        for (int i = 0; i < users.size(); i++) {
            users.get(i).UserAuthenticated(this);
        }
    }
    public  String name(String authMechName){
        return authMechName;
    }
    public int getUid(String username){
        int id=0;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUsername()==username){
                id = users.get(i).getId();
            }
        }
        return id;
    }
    public void execute(){
        user.check();
    }
}

// This is the concrete class of Template Method.
// Also 'ConcreteSubject' class of Observer pattern.
class Local extends AuthenticationMechanism{
    // There will be only one Local authentication mechanism.
    ArrayList<String> localUsers = new ArrayList<String>();

    private static Local local = null;
    public static Local getInstance(){
        if (local == null){
            local = new Local();
            local.name("Local");
        }return local;
    }
    private Local(){

    }

    @Override
    public int getUid(String username) {
        return super.getUid(username);
    }

    protected final int authenticate(String username, String password){
        // call this method when user authenticates successfully.
        System.out.println("User "+username+" is authenticated in Local successfully.");
        localUsers.add(username);
        Notify();
        return 0;
    }
    public void execute(){
        user.check();
    }
}

// This is the concrete class of Template Method.
// Also 'ConcreteSubject' class of Observer pattern.
class LDAP extends AuthenticationMechanism{
    ArrayList<String> ldapUsers = new ArrayList<String>();
    // There will be only one Local authentication mechanism.
    private static LDAP ldap = null;
    public static LDAP getInstance(){
        if (ldap == null){
            ldap = new LDAP();
            ldap.name("LDAP");
        }return ldap;
    }
    private LDAP(){
        this.setAuthMechName("ldap");
    }

    protected int authenticate(String username, String password){
        System.out.println("User "+username+" is authenticated in LDAP successfully.");
        ldapUsers.add(username);
        Notify();
        return 0;
    }
    @Override
    public int getUid(String username) {
        return super.getUid(username);
    }
    public void execute(){
        user.check();
    }

}

// This is the concrete class of Template Method.
// Also 'ConcreteSubject' class of Observer pattern.
class KERBEROS extends AuthenticationMechanism{
    ArrayList<String> kerberosUsers = new ArrayList<String>();
    // There will be only one KERBEROS authentication mechanism.
    private static KERBEROS kerberos = null;
    public static KERBEROS getInstance(){
        if (kerberos == null){
            kerberos = new KERBEROS();
        }return kerberos;
    }
    private KERBEROS(){
        this.setAuthMechName("kerberos");
    }
    protected int authenticate(String username, String password){
        System.out.println("User "+username+" is authenticated in KERBEROS successfully.");
        kerberosUsers.add(username);
        Notify();
        return 0;
    }
    @Override
    public int getUid(String username) {
        return super.getUid(username);
    }
    public void execute(){
        user.check();
    }
}

class PluggableAuthenticationMechanism{
    public static void main(String [] args){
        System.out.println("Welcome to Pluggable Authentication Mechanism");
        OperatingSystem os = OperatingSystem.getInstance();
        AuthenticationMechanism local = Local.getInstance();
        AuthenticationMechanism kerberos = KERBEROS.getInstance();
        AuthenticationMechanism ldap = LDAP.getInstance();

        ArrayList<User> users = new ArrayList<User>();

        ArrayList<User> localUsers = new ArrayList<User>();
        ArrayList<User> kerberosUsers = new ArrayList<User>();
        ArrayList<User> ldapUsers = new ArrayList<User>();


        User a = new User("Kubra", 1, "abc");
        User b = new User("Oktay", 2, "klm");
        User c = new User("Ali", 3, "xyz");

        users.add(a);
        users.add(b);
        users.add(c);

   //   local.authenticate("Kubra","abc");
        kerberos.authenticate("Ali","xyz");
        ldap.authenticate("Oktay","klm");

        local.Attach(a);
        kerberos.Attach(c);
        ldap.Attach(b);

        if(local.authenticate("Kubra","abc") == 0){
            localUsers.add(a);
        }
        kerberosUsers.add(c);
        ldapUsers.add(b);
        // see user id by username.
        // it returns 1 which is true.
        System.out.println(local.getUid("Kubra"));

        Invoker invoker = new Invoker();
        a.check();

        Iterator itr = users.iterator();
        // Prints the user adresses.
        while(itr.hasNext()) {
            Object element = itr.next();
            System.out.print(element + " ");
        }
    }
}
