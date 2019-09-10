package com.obaju;

import com.obaju.model.AjaxResponseBody;
import com.obaju.model.CartData;
import com.obaju.model.Order1;
import com.obaju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController //Restfull API(Application Programming Interface)
@RequestMapping("/api")
public class CartController {
    @Autowired
    ProductService productService;

    @PostMapping("/add-cart")
    public ResponseEntity<?> addCart(@RequestBody CartData cartData) {
        AjaxResponseBody results = new AjaxResponseBody();
        Order1 order = productService.getCurrentOrder();
        if (order == null) {
            order = productService.createNewOrder();
        }
        //create order detail
        productService.createOrUpdateOrderDetail(order.getId(),
                cartData.getProdId(), cartData.getQuantity());
        //update order detail
        double orderTotal = productService.getOrderTotal(order.getId());
        productService.updateOrderTotal(order.getId(), orderTotal);

        int totalItems = productService.getTotalItemInOrder();
        results.setTotalItems(totalItems);
        results.setMsg("Item added to cart");
        return ResponseEntity.ok(results);
    }

    @RequestMapping(value = "/get-total-items", produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> getTotalItem() {
        int totalItems = 0;
        AjaxResponseBody result = new AjaxResponseBody();
        //check if user has logged in
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            totalItems = productService.getTotalItemInOrder();
        }
        result.setTotalItems(totalItems);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update-basket")
    public ResponseEntity<?> updateBaseket(@RequestBody CartData cartData){
        AjaxResponseBody result = new AjaxResponseBody();
        List<Map> dataList = cartData.getDataList();
        Order1 order = productService.getCurrentOrder();
        for(Map map : dataList){
            int prodId = Integer.parseInt(map.get("prodId").toString());
            int quantity = Integer.parseInt(map.get("quantity").toString());
            //update order detail
            productService.updateOrderInList(order.getId(), prodId, quantity);
        }
        //update order total
        double orderTotal = productService.getOrderTotal(order.getId());
        productService.updateOrderTotal(order.getId(), orderTotal);

        return  ResponseEntity.ok(result);
    }
}
