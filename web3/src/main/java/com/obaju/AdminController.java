package com.obaju;

import com.obaju.model.Image;
import com.obaju.model.Product;
import com.obaju.model.ProductForm;
import com.obaju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Value("${upload.root.folder}")
    private String uploadRootFolder;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    ProductService productService;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminHome(Model model,
                            @RequestParam (name = "p", defaultValue = "1")
                                    int pageNumber,
                            @ModelAttribute(value = "product")ProductForm prodData){
        Map map = productService.getAllProducts(pageNumber);
        List<Product> list = (List<Product>) map.get("prodList");
        int totalPages = Integer.parseInt(map.get("totalPages").toString());
        List<Integer> pageNoList = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNoList.add(i);
        }
        model.addAttribute("pageNoList", pageNoList);
        model.addAttribute("prodList", list);
        model.addAttribute("currentPage", pageNumber - 1);
        return "admin/admin-home";
    }

    @GetMapping(value = "admin/add-new-product")
    public String addNewProductForm(@ModelAttribute(value = "product")ProductForm prodData){
        return "admin/add-new-product";
    }

    @PostMapping(value = "admin/add-new-product")
    public String addNewProduct(@ModelAttribute(value = "product")ProductForm prodData){
        Product p = new Product();
        saveProduct(p, prodData);

        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/remove-products")
    public String removeProducts(RedirectAttributes redirectAttributes,
                                 @ModelAttribute(value =  "product")ProductForm prodData){
        //check if order detail has product id
        List<Product> cannotRemove = new ArrayList<>();
        for (int prodId : prodData.getRemoveProducts()) {
            if(productService.isProductInOrderDetails(prodId)){
                Product product = productService.getProductById(prodId);
                cannotRemove.add(product);
            }else{
                productService.removeProduct(prodId);
            }
        }
        redirectAttributes.addFlashAttribute("cannotRemoveList",
                cannotRemove);
        return "redirect:/admin";
    }
    @GetMapping(value = "admin/update-product/{prodId}")
    public String updateProductForm(@ModelAttribute(value = "product")ProductForm prodData,
                                    @PathVariable int prodId, Model model){
        Product product = productService.getProductById(prodId);
        //init data for input fields
        prodData.setName(product.getName());
        prodData.setDesc(product.getDescription());
        prodData.setPrice(product.getPrice());
        prodData.setPgift(product.getIsGift() == 1 ? true : false);
        prodData.setPsale(product.getIsSale() == 1 ? true : false);
        prodData.setPnew(product.getIsNew() == 1 ? true : false);
        model.addAttribute("images", product.getImageList());
        model.addAttribute("prodId", prodId);
        return "admin/update-product";
    }

    @PostMapping(value = "admin/update-product/{prodId}")
    public String updateProduct(@PathVariable int prodId,
                                @ModelAttribute(value = "product")ProductForm prodData){
        Product p = productService.getProductById(prodId);
        saveProduct(p, prodData);
        return "redirect:/admin/update-product/" + prodId;
    }

    @GetMapping(value = "admin/remove-image/{prodId}/{imgId}")
    public String removeImage(@PathVariable int prodId, @PathVariable int imgId){
        productService.removeImage(imgId);
        return "redirect:/admin/update-product/" + prodId;
    }

    void saveProduct(Product product, ProductForm prodData){
        product.setName(prodData.getName());
        product.setDescription(prodData.getDesc());
        product.setPrice(prodData.getPrice());
        product.setIsGift(prodData.isPgift() ? 1 : 0);
        product.setIsNew(prodData.isPnew() ? 1 : 0);
        product.setIsSale(prodData.isPsale() ? 1 : 0);

        List<Image> imageProductList = new ArrayList<>();
        //upload images
        MultipartFile[] imageList = prodData.getImages();
        if(imageList.length > 0){
            for (MultipartFile file : imageList){
                String productImageName =  uploadPath + String.format("%s-%s",
                        System.currentTimeMillis(),
                        file.getOriginalFilename());
                String upload_dir = uploadRootFolder + productImageName;
                try {
                    file.transferTo(new File(upload_dir));
                    Image image = new Image(productImageName, product);
                    imageProductList.add(image);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            product.setImageList(imageProductList);
        }
        productService.addNewProduct(product);
    }
}