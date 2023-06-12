package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerTest {
    private CartController cartController;
    private ItemController itemController;
    private OrderController orderController;
    private UserController userController;
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CartRepository cartRepository = Mockito.mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

    @Before
    public void setUp() {
        userController = new UserController();
        itemController = new ItemController();
        cartController = new CartController();
        orderController = new OrderController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
        TestUtils.injectObject(orderController, "userRepository", userRepository);
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("son");
        createUserRequest.setPassword("123123123");
        createUserRequest.setConfirmPassword("123123123");
        Mockito.when(bCryptPasswordEncoder.encode(Mockito.any())).thenReturn("hashing");
        ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);
        Assert.assertEquals(createUserRequest.getUsername(), userResponseEntity.getBody().getUsername());
        Assert.assertNotNull(userResponseEntity);
        User user = userResponseEntity.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getId());
        Assert.assertEquals("son", user.getUsername());
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setUsername("son");
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(optionalUser);
        ResponseEntity<User> userResponseEntity = userController.findById(1L);
        User userReturn = userResponseEntity.getBody();
        Assert.assertEquals(userReturn.getUsername(), user.getUsername());
    }

    @Test
    public void testGetUserByName() {
        User user = new User();
        user.setUsername("son");
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);
        ResponseEntity<User> userResponseEntity = userController.findByUserName("son");
        User userReturn = userResponseEntity.getBody();
        Assert.assertEquals(userReturn.getUsername(), user.getUsername());
    }

    @Test
    public void testAddToCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("son");
        request.setItemId(1);
        request.setQuantity(1);
        User user = new User();
        user.setUsername("son");
        user.setPassword("123123123");
        user.setId(1);
        Item item = new Item();
        item.setPrice(new BigDecimal(1));
        item.setId(1L);
        item.setName("car");
        Optional<Item> optionalItem = Optional.of(item);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.addItem(item);
        user.setCart(cart);
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(optionalItem);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        Assert.assertNotNull(response.getBody());
        Cart cartResp = response.getBody();
        Assert.assertEquals(cartResp.getItems().get(0), cart.getItems().get(0));
    }

    @Test
    public void testRemoveFromCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("son");
        request.setItemId(1);
        request.setQuantity(1);
        User user = new User();
        user.setUsername("son");
        user.setPassword("123123123");
        user.setId(1);
        Item item = new Item();
        item.setPrice(new BigDecimal(1));
        item.setId(1L);
        item.setName("car");
        Optional<Item> optionalItem = Optional.of(item);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.addItem(item);
        user.setCart(cart);
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(optionalItem);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        Assert.assertNotNull(response.getBody());
        Cart cartResp = response.getBody();
        Assert.assertTrue(cartResp.getItems().isEmpty());
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        items.add(item);
        Mockito.when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> itemList = response.getBody();
        Assert.assertEquals(itemList.get(0), items.get(0));
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setName("car");
        item.setId(1L);
        Optional<Item> optionalItem = Optional.of(item);
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(optionalItem);
        ResponseEntity<Item> response = itemController.getItemById(1L);
        Item responseItem = response.getBody();
        Assert.assertEquals(responseItem.getId(), item.getId());
        Assert.assertEquals(responseItem.getName(), item.getName());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("car");
        items.add(item);
        Mockito.when(itemRepository.findByName(Mockito.any())).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("car");
        List<Item> itemList = response.getBody();
        Assert.assertEquals(itemList.get(0), items.get(0));
    }

    @Test
    public void testSubmitOrder() {
        User user = new User();
        user.setUsername("son");
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.setTotal(new BigDecimal(1));
        cart.setUser(user);
        user.setCart(cart);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);
        ResponseEntity<UserOrder> userResponseEntity = orderController.submit("son");
        UserOrder userReturn = userResponseEntity.getBody();
        Assert.assertEquals(userReturn.getUser().getUsername(), user.getUsername());
        Assert.assertEquals(userReturn.getTotal(), cart.getTotal());
    }

    @Test
    public void testGetOrdersForUser() {
        User user = new User();
        user.setUsername("son");
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setId(1L);
        userOrder.setTotal(new BigDecimal(1));
        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(userOrder);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);
        Mockito.when(orderRepository.findByUser(user)).thenReturn(userOrders);
        ResponseEntity<List<UserOrder>> userResponseEntity = orderController.getOrdersForUser("son");
        List<UserOrder> userReturn = userResponseEntity.getBody();
        Assert.assertEquals(userReturn.get(0).getUser(), user);
        Assert.assertEquals(userReturn.get(0).getTotal(), userOrder.getTotal());
        Assert.assertEquals(userReturn.get(0).getId(), userOrder.getId());
    }

}
