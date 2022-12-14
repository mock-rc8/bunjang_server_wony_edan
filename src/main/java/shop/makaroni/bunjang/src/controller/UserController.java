package shop.makaroni.bunjang.src.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.makaroni.bunjang.src.domain.inquiry.dto.InquirySaveRequest;
import shop.makaroni.bunjang.src.domain.item.State;
import shop.makaroni.bunjang.src.domain.user.dto.PatchUserRequest;
import shop.makaroni.bunjang.src.domain.user.dto.SaveUserRequest;
import shop.makaroni.bunjang.src.domain.user.view.MyStoreResponse;
import shop.makaroni.bunjang.src.domain.user.view.StoreInfoView;
import shop.makaroni.bunjang.src.domain.user.view.StoreSaleView;
import shop.makaroni.bunjang.src.domain.user.view.StoreSearchView;
import shop.makaroni.bunjang.src.provider.UserProvider;
import shop.makaroni.bunjang.src.response.ResponseInfo;
import shop.makaroni.bunjang.src.response.exception.EmptyParamEx;
import shop.makaroni.bunjang.src.service.InquiryService;
import shop.makaroni.bunjang.src.service.UserService;
import shop.makaroni.bunjang.utils.PagingCond;
import shop.makaroni.bunjang.utils.QueryStringArgResolver;
import shop.makaroni.bunjang.utils.auth.Login;
import shop.makaroni.bunjang.utils.auth.Secured;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static shop.makaroni.bunjang.src.response.ErrorCode.EMPTY_PARAM_EXCEPTION;
import static shop.makaroni.bunjang.src.response.SuccessStatus.PATCH_SUCCESS;
import static shop.makaroni.bunjang.src.response.SuccessStatus.SAVE_SUCCESS;
import static shop.makaroni.bunjang.src.response.SuccessStatus.WITHDRAWAL_SUCCESS;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProvider userProvider;
    private final UserService userService;
    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ResponseInfo> save(@Valid @RequestBody SaveUserRequest request) {
        Long userIdx = userService.save(request);
        String url = "/users/" + userIdx;
        return ResponseEntity.created(URI.create(url)).body(ResponseInfo.of(SAVE_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<MyStoreResponse> getMyStore(@Login Long userIdx) {
        return ResponseEntity.ok(userProvider.getMyStore(userIdx));
    }

    @GetMapping("/items")
    public ResponseEntity<List<StoreSaleView>> getMyStoreItem(@Login Long userIdx,
                                                              @RequestParam("condition") String condition,
                                                              @QueryStringArgResolver PagingCond pagingCond) {
        State.valid(condition);
        return ResponseEntity.ok(userProvider.getMyStoreItem(userIdx, condition, pagingCond));
    }

    @GetMapping("/items/search")
    public ResponseEntity<List<StoreSaleView>> getMyStoreItem(@Login Long userIdx,
                                                              @RequestParam(value = "itemName", defaultValue = "") String itemName,
                                                              @RequestParam("condition") String condition,
                                                              @QueryStringArgResolver PagingCond pagingCond) {
        State.valid(condition);
        return ResponseEntity.ok(userProvider.searchStoreItemByName(userIdx, itemName, condition, pagingCond));
    }

    @PatchMapping
    public ResponseEntity<ResponseInfo> update(@Login Long userIdx, @Valid @RequestBody PatchUserRequest request) {
        userService.update(userIdx, request);
        return ResponseEntity.ok(ResponseInfo.of(PATCH_SUCCESS));
    }

    @PatchMapping("/d")
    public ResponseEntity<ResponseInfo> delete(@Login Long userIdx) {
        userService.delete(userIdx);
        return ResponseEntity.ok(ResponseInfo.of(WITHDRAWAL_SUCCESS));
    }

    @PostMapping("/inquiries/{storeIdx}")
    public ResponseEntity<ResponseInfo> saveInquiry(@Login Long userIdx, @PathVariable Long storeIdx, @Valid @RequestBody InquirySaveRequest request) {
        Long inquiryIdx = inquiryService.save(userIdx, storeIdx, request);
        String uri = "/users/" + userIdx + "/stores/" + storeIdx + "/inquiries/" + inquiryIdx;
        return ResponseEntity.created(URI.create(uri)).body(ResponseInfo.of(SAVE_SUCCESS));
    }

    @Secured
    @GetMapping("/stores/{storeIdx}")
    public ResponseEntity<StoreInfoView> getStoreById(@PathVariable Long storeIdx) {
        return ResponseEntity.ok(userProvider.getStoreById(storeIdx));
    }

    @Secured
    @GetMapping("/stores/search")
    public ResponseEntity<List<StoreSearchView>> searchStoreByName(@RequestParam String name) {
        if (name.isBlank()) {
            throw new EmptyParamEx(EMPTY_PARAM_EXCEPTION.getMessages());
        }
        return ResponseEntity.ok(userProvider.searchStoreByName(name));
    }
}
