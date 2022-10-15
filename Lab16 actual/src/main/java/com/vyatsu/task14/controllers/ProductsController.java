package com.vyatsu.task14.controllers;

import com.vyatsu.task14.entities.Product;
import com.vyatsu.task14.repositories.ProductRepository;
import com.vyatsu.task14.repositories.ProductSpecs;
import com.vyatsu.task14.services.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.stream.IntStream;

@Controller
public class ProductsController {
    private ProductsService productsService;
    private ProductRepository productRepository;
    private int pagees = 0;
    @Autowired
    public void setProductsService(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("")
    public String Firste()
    {
        return "redirect:/products?page=0";
    }



    @GetMapping("/products")
    public String ShowProducts(@RequestParam(defaultValue = "3",required = false) int size,
                               @RequestParam(defaultValue = "0",required = false) int page,
                               Model model)
    {


        Page<Product> productPage;

//        productPage = productsService.getFilteredList(X, PageRequest.of(page, 3));
        productPage = productsService.pageGetAll(PageRequest.of(page, size));
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("products", productPage);
        model.addAttribute("productPage", productPage);
        model.addAttribute("numbers", IntStream.range(0, productPage.getTotalPages()).toArray());
//        model.addAttribute("Min", Min);
//        model.addAttribute("Max", Max);
//        model.addAttribute("Substring", Substring);
        model.addAttribute("top3",productsService.top3());

        return "hello";
    }

    @GetMapping("/add")
    public String addProduct(Model model)
    {
//        productsService.add(Name,Price);
        Product product = new Product();
        model.addAttribute("product",product);
//        productsService.saveOrUpdate(product);
        return "edit";
    }

    @GetMapping("/main")
    public String gotoMain(Model model)
    {
        return "main";
    }
//
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") long id)
    {
        productsService.deleteById(id);
        return "redirect:/products?Min=&Max=&Substring=";
    }
//
//    @GetMapping("/change")
//    public String changeProduct(@RequestParam(value = "ID") Integer id,
//                                @RequestParam(value = "Name",required = false) String Name,
//                                @RequestParam(value = "Price",required = false) Integer Price)
//    {
//        productsService.changeById(id,Name,Price);
//        return "redirect:/products?Min=&Max=&Substring=";
//    }
//    @GetMapping("/show/{id}")
//    public String showOneProduct(Model model, @PathVariable(value = "id") long id) {
//        Product product = productsService.getbyId(id);
//        model.addAttribute("product", product);
//        return "product-page";
//    }
    @GetMapping("/showPage/{id}")
    public String showPageProduct(Model model, @PathVariable(value = "id") int id) {

        pagees = id;
        System.out.println(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println(currentPrincipalName);
        return "redirect:/products?Min=&Max=&Substring=";
    }
    private static HashMap<Long,Long> County = new HashMap<>();

//    public void SoutLikeProducts(){
//        ArrayList<Long> keys = new ArrayList<>(County.keySet());
//        ArrayList<Long> values = new ArrayList<>(County.values());
//        for (int i = 0; i < values.size(); i++) {
//            System.out.println("ID самых просматриваемых товаров " + keys.get(i));
//        }
//    }
//версия если работает просмотр информации
//    @GetMapping("/products/show/{id}")
//    public String showOneProduct(Model model, @PathVariable(value = "id") long id)
//    {
//        Product product = productsService.getbyId(id);
//        model.addAttribute("product", product);
//        System.out.println("Посмотрели товар с id " + id);
//        if(County.get(id) != null) County.put(id, County.get(id) + 1);
//        else County.put(id, 1L);
//      //  SoutLikeProducts();
//        Long max = County.entrySet()
//                .stream()
//                .max((entry1,
//                      entry2) -> entry1.getValue() >
//                        entry2.getValue() ? 1 : -1)
//                .get().getKey();
//        System.out.println("Самый просматриваемый id товара " + max + " кол-во просмотров " + County.get(max));
//        return "product-page";
//    }


    @PostMapping("/product/edit")
    public String editProduct(@ModelAttribute(value = "product")Product product) {
        productsService.saveOrUpdate(product);
        return "redirect:/products?Min=&Max=&Substring=";
    }
    @GetMapping("/show_edit/{id}")
    public String editOneProduct(Model model, @PathVariable(value = "id") int id) {
        Product product = productsService.getbyId((long)id);
        System.out.println("POPAL V CONTORL GET MAPPIN");
        model.addAttribute("product", product);
        return "edit";
    }
    // Версия если работает подсчёт
   // @GetMapping("/show/{id}")
    @GetMapping("/products/show/{id}")
    public String showProduct(Model model, @PathVariable(value = "id") Long id) {
        Product product = productsService.getById(id);
        productsService.saveShowed(product);
        model.addAttribute("product", product);
        return "product-page";
    }

    @PostMapping("products/find")
    public String filterProduct(
            @RequestParam String title,
            @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice,
            @RequestParam(required = false, defaultValue = "3") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model
    ) {
        Page<Product> productPage;


        Specification<Product> X = Specification.where(null);
        if(maxPrice != null)
        {
            X = X.and(ProductSpecs.priceLesserThanOrEq(BigDecimal.valueOf(maxPrice)));
        }
        if(minPrice != null)
        {
            X = X.and(ProductSpecs.priceGreaterThanOrEq(BigDecimal.valueOf(minPrice)));
        }
        if(title != "")
        {
            X = X.and(ProductSpecs.titleContainsWord(title));
        }

        productPage = productsService.getFilteredList(X, PageRequest.of(page, size));
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("products", productPage);
        model.addAttribute("productPage", productPage);
        model.addAttribute("numbers", IntStream.range(0, productPage.getTotalPages()).toArray());
        model.addAttribute("fTitle", title);
        model.addAttribute("fMinPrice", minPrice);
        model.addAttribute("fMaxPrice", maxPrice);
        return "/hello";
    }

//    @GetMapping("/top3")
//    public String top3()
//    {
//
//    }

    @GetMapping("/res")
    public String reset(){
        return "redirect:/products?Min=&Max=&Substring=";
    }
}
