package pl.sda.spring_start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import pl.sda.spring_start.model.Category;
import pl.sda.spring_start.model.Post;
import pl.sda.spring_start.model.User;
import pl.sda.spring_start.service.PostService;
import pl.sda.spring_start.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// klasa mapująca żądania prokołu http - adres lokalny http://localhost:8080
//@Controller       //- mapuje żądanie i zwraca widok html
@RestController     //- mapuje żądania i dane REST - Representative State Transfer
public class BlogRESTController {
    UserService userService;
    PostService postService;

    @Autowired              // wstrzykiwanie zależności przez konstturktor
    public BlogRESTController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/user/register")
    public void registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        User user = new User(email, password, LocalDateTime.now(), false);
        userService.registerUser(user);
    }

    @PutMapping("/user/registerConfirm")
    public void registerConfirm(
            @RequestParam("userId") int userId
    ) {
        userService.activateUser(userId);
    }

    @DeleteMapping("/user/delete")
    public void deleteUserById(
            @RequestParam("userId") int userId
    ) {
        try {
            userService.deleteUser(userId);
        } catch (EmptyResultDataAccessException e) {

        }
    }
    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsersOrderByRegistrationDateTimeDesc();
    }
    @GetMapping("/user/email={email}")
    public User getUserById(
            @RequestParam("email") String email
    ){
        return userService.getUserByEmail(email).orElse(new User());
    }
    @PostMapping("/post/addPost")
    public void addPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") Category category,
            @RequestParam("userId") int userId
    ){
        Optional<User> userOptional = userService.getUserById(userId);
        if(userOptional.isPresent()){
            if(userOptional.get().isStatus()){
                postService.addPost(title, content, category, userOptional.get());
            }
        }
    }
    @GetMapping("/posts")
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }



    // GET      - SELECT - pobiera zawartość z bazy i zwraca obiekt lub listę obiektów
    // POST     - INSERT - wprowadza dane do bazy i nic nie zwraca
    // PUT      - UPDATE - edytuje dane z bazy i nic nie zwraca
    // DELETE   - DELETE - usuwanie obiektu lub listy obiektów z bazy
    @GetMapping("/")                         // localhost:8080/
    public String home() {
        return "Hello in homepage";
    }

    @GetMapping("/home/{name}&{status}")     // localhost:8080/home/Michal&true
    public String homeWithName(
            @PathVariable("name") String name,
            @PathVariable("status") Boolean status
    ) {
        return status ? "Hello " + name + " in hompage" : "Your account is locked";
    }

    @GetMapping("/credentials")         //localhost:8080/credentials?login=miki&password=qwe123
    public String getCredentials(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        return String.format("login : %s \npassword : %s", login, password);
    }

    @GetMapping("/posts/byCategory")
    public List<Post> getPostsByCategory(
            @RequestParam("category") Category category
    ){
        return postService.getPostsByCategory(category);
    }
    @GetMapping("/posts/ByCategoryAndAtuhor")
    public List<Post> getPostsByCategoryAndAuthor(
            @RequestParam("category") Category category,
            @RequestParam("author") User author
    ){
        return postService.getPostsByCategoryAndAuthor(category,author);
    }

}
