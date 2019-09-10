package com.obaju.service;

import com.obaju.model.Order1;
import com.obaju.model.OrderDetail;
import com.obaju.model.Product;
import com.obaju.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Repository
@Transactional
public class ProductService {
    public int ORDER_STATUS_NEW = 0;
    public int ORDER_STATUS_DELIVERED = 1;
    private int PAGE_SIZE = 3;
    @Autowired
    UserService userService;

    @Autowired
    ProductPaging productPaging;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ImageRepository imageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    //Admin pages
    public Product addNewProduct(Product product) {
        return productRepository.save(product);
    }

    public void removeProduct(int prodId) {
        productRepository.deleteById(prodId);
    }

    public void removeImage(int imgId) {
        imageRepository.deleteById(imgId);
    }

    public boolean isProductInOrderDetails(int prodId) {
        List list = entityManager.createQuery("From OrderDetail Where " +
                "productId.id = :productId")
                .setParameter("productId", prodId)
                .getResultList();
        if (list.size() > 0) return true;
        return false;
    }

    public List<Product> getAllProducts() {
        List<Product> list = null;
        list = entityManager.createQuery("select p From Product p").getResultList();
        return list;
    }

    public Map getAllProducts(int pageNumber){
        Map map = new HashMap();
        PageRequest pageRequest = PageRequest.of(pageNumber - 1,
                PAGE_SIZE, Sort.Direction.ASC, "price");
        int totalRecords = (int) productPaging.count();
        int totalPages = (int)Math.ceil(totalRecords/(double)PAGE_SIZE);
        map.put("prodList", productPaging.findAll(pageRequest).getContent());
        map.put("totalPages", totalPages);
        return map;
    }

    public Product getProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    public Order1 createNewOrder() {
        User user = userService.getCurrentUser();
        Order1 order = new Order1();
        order.setStatus(ORDER_STATUS_NEW);
        order.setUserId(user);
        order.setCreationDate(new Date());
        order.setTotal(0);
        return entityManager.merge(order);
    }

    public Order1 getCurrentOrder() {
        User user = userService.getCurrentUser();
        List<Order1> list = entityManager.createQuery("From Order1 Where " +
                "userId.id = :userId And status = :status")
                .setParameter("userId", user.getId())
                .setParameter("status", ORDER_STATUS_NEW)
                .getResultList();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public void updateOrderTotal(int orderId, double total) {
        Order1 order = entityManager.find(Order1.class, orderId);
        //order.setStatus(ORDER_STATUS_DELIVERED);
        order.setTotal(total);
        entityManager.merge(order);
    }

    public void checkoutOrder(int orderId) {
        Order1 order = entityManager.find(Order1.class, orderId);
        order.setStatus(ORDER_STATUS_DELIVERED);
        entityManager.merge(order);
    }

    public double getOrderTotal(int orderId) {
        String query = "Select sum(p.total) From OrderDetail p " +
                "Where p.orderId.id = :orderId";
        List<Double> list = entityManager.createQuery(query)
                .setParameter("orderId", orderId)
                .getResultList();
        if (list.size() > 0) return list.get(0);
        return 0;
    }

    public int getTotalItemInOrder() {
        Order1 order = getCurrentOrder();
        if (order == null) return 0;
        int orderId = order.getId();
        String query = "Select sum(p.quantity) from OrderDetail p " +
                "Where p.orderId.id = :orderId";
        List list = entityManager.createQuery(query)
                .setParameter("orderId", orderId)
                .getResultList();

        if (list.size() > 0) return ((Long) list.get(0)).

                intValue();
        return 0;
    }

    public OrderDetail getOrderDetailByProAndOrder(int orderId, int prodId) {
        String query = "From OrderDetail p " +
                "Where p.orderId.id = :orderId and p.productId.id = :prodId";
        List<OrderDetail> list = entityManager.createQuery(query)
                .setParameter("orderId", orderId)
                .setParameter("prodId", prodId)
                .getResultList();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public Order1 getOrderById(int orderId) {
        return entityManager.find(Order1.class, orderId);
    }

    public void createOrUpdateOrderDetail(int orderId, int prodId, int quantity) {
        OrderDetail orderDetail = getOrderDetailByProAndOrder(orderId, prodId);
        Product product = getProductById(prodId);
        Order1 order = getOrderById(orderId);
        if (orderDetail == null) {
            orderDetail = new OrderDetail();
            orderDetail.setOrderId(order);
            orderDetail.setProductId(product);
            orderDetail.setTotal(quantity * product.getPrice());
            orderDetail.setQuantity(quantity);
        } else {
            quantity += orderDetail.getQuantity();
            orderDetail.setTotal(quantity * product.getPrice());
            orderDetail.setQuantity(quantity);
        }
        entityManager.merge(orderDetail);
    }

    public List<OrderDetail> getOrderDetailList() {
        List list = new ArrayList();
        Order1 order = getCurrentOrder();
        if (order == null) return list;
        String query = "From OrderDetail p where p.orderId.id = :orderId";
        list = entityManager.createQuery(query)
                .setParameter("orderId", order.getId()).getResultList();
        return list;
    }

    public void updateOrderInList(int orderId, int prodId, int quantity) {
        OrderDetail orderDetail = getOrderDetailByProAndOrder(orderId, prodId);
        Product product = getProductById(prodId);
        orderDetail.setTotal(quantity * product.getPrice());
        orderDetail.setQuantity(quantity);
        entityManager.merge(orderDetail);
    }
}
