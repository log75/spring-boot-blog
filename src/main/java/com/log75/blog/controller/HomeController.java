package com.log75.blog.controller;

import com.log75.blog.model.Post;
import com.log75.blog.model.User;
import com.log75.blog.repository.PostRepository;
import com.log75.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by alireza on 6/17/20.
 */

@Controller
public class HomeController {

    private UserRepository userRepository;
    private PostRepository postRepository;

    @Autowired
    public HomeController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping({"/home", "/"})
    public ModelAndView getHome() {
        ModelAndView modelAndView = new ModelAndView("welcome");
        modelAndView.addObject("posts", postRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/new-post")
    public String getNewPost(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());

        if (user != null) {
            Post post = new Post();
            post.setUser(user);
            model.addAttribute("post", post);
            return "/new-post";

        } else {
            return "/error";
        }
    }

    @PostMapping("/new-post")
    public String newPost(@ModelAttribute @Valid Post post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/new-post";
        }
        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping(value = "/post-detail/{id}")
    public ModelAndView getPostDetail(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("post-detail");
        Post post = postRepository.findById(id).get();
        modelAndView.addObject("post", post);
        return modelAndView;
    }

    @GetMapping(value = "/{username}/my-posts")
    public ModelAndView getMyPosts(@PathVariable("username") String username) {
        ModelAndView modelAndView = new ModelAndView("my-posts");
        User user = userRepository.findByUsername(username);
        modelAndView.addObject("myPosts", user.getPosts());
        return modelAndView;
    }

}
