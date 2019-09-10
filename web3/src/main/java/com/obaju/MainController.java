package com.obaju;

import com.obaju.model.*;
import com.obaju.service.ProductService;
import com.obaju.service.UserService;
import com.obaju.util.UserFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    UserFormValidator userFormValidator;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    //set form validator
    @InitBinder("userFormData")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(userFormValidator);
    }

    @GetMapping("/register")
    public String register(@ModelAttribute(value = "user") UserForm user) {
        return "register";
    }

    @PostMapping("/register")
    public String submitRegister(@Valid @ModelAttribute(value = "user") UserForm user,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/register";
        } else {
            String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            User newUser = new User(user.getName(), user.getEmail(), encryptedPassword);
            Role role = userService.getRole("ROLE_USER");
            newUser.setRole(role);
            userService.create(newUser);
            redirectAttributes.addFlashAttribute("msg",
                    "New account has been created");
            return "redirect:/register";
        }
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model,
                       @RequestParam(name = "p", defaultValue = "1") int pageNumber) {
        Map map = productService.getAllProducts(pageNumber);
        //List<Product> list = productService.getAllProducts();
        List<Product> list = (List<Product>) map.get("prodList");
        int totalPages = Integer.parseInt(map.get("totalPages").toString());
        List<Integer> pageNoList = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNoList.add(i);
        }
        model.addAttribute("pageNoList", pageNoList);
        model.addAttribute("prodList", list);
        model.addAttribute("currentPage", pageNumber - 1);
        return "home";
    }

    @GetMapping("/basket")
    public String basket() {
        return "basket";
    }

    @RequestMapping(value = "/get-order-list", method = RequestMethod.GET)
    public String getOrderList(Model model) {
        List<OrderDetail> orderList = productService.getOrderDetailList();
        double totalPrice = productService.getCurrentOrder().getTotal();
        int totalItems = productService.getTotalItemInOrder();

        model.addAttribute("orderList", orderList);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", totalItems);

        return "fragments/order_list :: order_list_content";
    }

    @GetMapping("/checkout/{orderId}")
    public String checkout(@PathVariable int orderId) {
        productService.checkoutOrder(orderId);
        return "redirect:/";
    }

    @GetMapping("/customer-account")
    public String customerAccount() {
        return "customer-account";
    }

    @GetMapping("/customer-orders")
    public String orderList() {
        return "customer-orders";
    }

    @GetMapping("/detail/{prodId}")
    public String detail(@PathVariable int prodId, Model model) {
        Product product = productService.getProductById(prodId);
        model.addAttribute("prod", product);
        return "detail";
    }

    @GetMapping("/customer-order")
    public String order() {
        return "customer-order";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Invalid login information");
        return "login";
    }

}
