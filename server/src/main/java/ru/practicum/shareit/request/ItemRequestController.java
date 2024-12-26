package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllAnotherUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.ok(itemRequestService.getAllAnotherUserRequests(requestedUserId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getOnlyThisUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.ok(itemRequestService.getOnlyThisUserRequests(requestedUserId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(@PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(requestId));
    }

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> addRequest(@RequestBody ItemRequestDto itemRequest,
                                                             @RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestService.addRequest(itemRequest, requestedUserId));
    }

}
