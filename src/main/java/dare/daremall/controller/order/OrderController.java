package dare.daremall.controller.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import dare.daremall.controller.member.auth.LoginUserDetails;
import dare.daremall.domain.*;
import dare.daremall.domain.discountPolicy.DiscountPolicy;
import dare.daremall.repository.BaggedItemRepository;
import dare.daremall.service.MemberService;
import dare.daremall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {

    private final MemberService memberService;
    private final OrderService orderService;
    private final BaggedItemRepository baggedItemRepository;
    private final DiscountPolicy discountPolicy;

    /** 사용자 기능 **/

    @GetMapping(value = "/new/{orderOption}")
    public String orderForm(@AuthenticationPrincipal LoginUserDetails member,
                            @PathVariable("orderOption") String option,
                            Model model) {

        if(member==null) return "redirect:/members/login";

        if(option.equals("all")) {
            memberService.selectAllBagItem(member.getUsername());
        }

        List<BaggedItemOrderDto> baggedItem = baggedItemRepository.findSelected(member.getUsername())
                .stream().map(bi -> new BaggedItemOrderDto(bi))
                .collect(Collectors.toList());

        if(baggedItem.size()==0) {
            return "redirect:/shop";
        }

        model.addAttribute("items", baggedItem);
        int totalItemPrice = baggedItem.stream().mapToInt(bi->bi.getTotalPrice()).sum();
        int shippingFee = discountPolicy.isDiscountShip(totalItemPrice)==true? 0:2500;
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("myDeliveries", memberService.findUser(member.getUsername()).getDeliveryInfos().stream().map(di -> new DeliveryInfoDto(di)).collect(Collectors.toList()));

        model.addAttribute("totalItemPrice", totalItemPrice);
        model.addAttribute("totalPrice", totalItemPrice+shippingFee);
        model.addAttribute("orderForm", new OrderForm());

        model.addAttribute("paymentForm", new PaymentForm(memberService.findUser(member.getUsername())));
        return "/user/order/orderForm";
    }

    @PostMapping(value = "/new/addItem")
    public String orderFormWithItem(@AuthenticationPrincipal LoginUserDetails member,
                                    @RequestParam(value = "itemId", required = false) Long itemId,
                                    @RequestParam(value = "count", required = false, defaultValue = "0") int count) {
        if(member==null) return "redirect:/members/login";

        memberService.addShoppingBag(itemId, member.getUsername(), count);
        return "redirect:/order/new/all";
    }

    @PostMapping(value = "/createOrder")
    public String createOrder(@AuthenticationPrincipal LoginUserDetails member,
                              @Validated OrderForm orderForm, BindingResult result) {

        if(member==null) return "redirect:/members/login";
        if(result.hasErrors()){
            return "redirect:/order/new/select";
        }

        Long orderId = orderForm.getPayment().equals("KAKAO")? orderService.createOrder(member.getUsername(), orderForm, OrderStatus.PAY, orderForm.getMerchantUid(), orderForm.getImpUid())
                :orderService.createOrder(member.getUsername(), orderForm, OrderStatus.ORDER, null, null);

        return "redirect:/order/success/"+orderId;
        //return "redirect:/order/payment";
    }

    @GetMapping(value = "/success/{orderId}")
    public String orderSuccess(@AuthenticationPrincipal LoginUserDetails member,
                               @PathVariable("orderId") Long orderId, Model model) {

        if(member==null) return "redirect:/members/login";

        OrderDto orderDto = new OrderDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDto);

        return "/user/order/orderSuccess";
    }

    @GetMapping(value = "/detail")
    public String orderDetail(@AuthenticationPrincipal LoginUserDetails member,
                              @RequestParam("orderId") Long orderId, Model model) {

        if(member==null) return "redirect:/members/login";

        OrderDetailDto orderDetailDto = new OrderDetailDto(orderService.findOne(orderId));
        model.addAttribute("order", orderDetailDto);

        return "/user/order/orderDetail";
    }

    @PostMapping(value = "/cancel")
    public String cancelOrder(@AuthenticationPrincipal LoginUserDetails member,
                              Long orderId) {
        if(member==null) return "redirect:/members/login";
        Order findOrder = orderService.findOne(orderId);
        if(cancelPayment(findOrder.getMerchantUid()) == 1) {
            orderService.cancelOrder(orderId);
        }
        else {
            throw new IllegalStateException("환불에 실패했습니다.");
        }
        return "redirect:/userinfo/orderList";
    }

    @PostMapping(value = "/delete")
    public String deleteOrder(@AuthenticationPrincipal LoginUserDetails member,
                              Long orderId) {
        if(member==null) return "redirect:/members/login";
        orderService.deleteOrder(orderId);
        return "redirect:/userinfo/orderList";
    }


    /** for iamport **/

    public String getImportToken() {
        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.iamport.kr/users/getToken");
        Map<String,String> m  = new HashMap<String,String>();
        m.put("imp_key", "7850918775710695");
        m.put("imp_secret", "4c02feb6adbf7e576849ea0abb51c0c5a4ba50d730aa99ef219d0b459a44a5fff88d3b433a45efc0");
        try {
            post.setEntity(new UrlEncodedFormEntity(convertParameter(m)));
            HttpResponse res = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            String body = EntityUtils.toString(res.getEntity());
            JsonNode rootNode = mapper.readTree(body);
            JsonNode resNode = rootNode.get("response");
            result = resNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // 출처: https://zarawebstudy.tistory.com/11 [자라월드:티스토리]


    public int cancelPayment(String mid) {
        String token = getImportToken();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.iamport.kr/payments/cancel");
        Map<String, String> map = new HashMap<String, String>();
        post.setHeader("Authorization", token);
        map.put("merchant_uid", mid);
        String resp = "";
        try {
            post.setEntity(new UrlEncodedFormEntity(convertParameter(map)));
            HttpResponse res = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            String enty = EntityUtils.toString(res.getEntity());
            JsonNode rootNode = mapper.readTree(enty);
            resp = rootNode.get("response").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resp.equals("null")) {
            // System.err.println("환불실패");
            return -1;
        } else {
            // System.err.println("환불성공");
            return 1;

            // 출처:https://zarawebstudy.tistory.com/11 [자라월드:티스토리]
        }
    }

    private List<NameValuePair> convertParameter(Map<String,String> paramMap){
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();

        Set<Map.Entry<String,String>> entries = paramMap.entrySet();

        for(Map.Entry<String,String> entry : entries) {
            paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return paramList;
    }
    // 출처: https://zarawebstudy.tistory.com/11 [자라월드:티스토리]

    @ResponseBody
    @PostMapping(value="/verifyIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@AuthenticationPrincipal LoginUserDetails member, @PathVariable(value= "imp_uid") String imp_uid) throws Exception {
        return new IamportClient("7850918775710695", "4c02feb6adbf7e576849ea0abb51c0c5a4ba50d730aa99ef219d0b459a44a5fff88d3b433a45efc0").paymentByImpUid(imp_uid);
    }

    public void setHackCheck(String amount, String mId) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.iamport.kr/payments/prepare");
        Map<String,String> m  =new HashMap<String,String>();
        post.setHeader("Authorization", getImportToken());
        m.put("amount", amount);
        m.put("merchant_uid", mId);
        try {
            post.setEntity(new UrlEncodedFormEntity(convertParameter(m)));
            HttpResponse res = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            String body = EntityUtils.toString(res.getEntity());
            JsonNode rootNode = mapper.readTree(body);
            // System.out.println(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 관리자 페이지 **/

    @PostMapping(value = "/findOrder")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody UpdateOrderDto findOrder(@RequestParam("orderId") Long orderId){
        Order findOrder = orderService.findOne(orderId);
        return new UpdateOrderDto(findOrder);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateOrder(UpdateOrderDto updateOrderDto) {
        orderService.update(updateOrderDto);
        return "redirect:/admin/order";
    }
}
