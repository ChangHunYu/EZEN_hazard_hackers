package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.itinerary.Itinerary;
import ezen.risk_buster.hazard_hackers.itinerary.ItineraryRepository;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    public ChecklistDto getChecklistByItineraryId(String userEmail, Long itineraryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        Checklist checklist = checklistRepository.findByItinerary_Id(itineraryId);

        if (checklist == null) {
            return null;
        }

        // 사용자 권한 확인
        if (!checklist.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to access this checklist");
        }

        return mapToChecklistDto(checklist);
    }
    private ChecklistDto mapToChecklistDto(Checklist checklist) {
        return new ChecklistDto(
                checklist.getId(),
                checklist.getUser().getId(),
                checklist.getItinerary() != null ? checklist.getItinerary().getId() : null,
                checklist.getTitle(),
                mapToItemDtos(checklist),
                checklist.isDeleted()
        );
    }

    private List<ItemDto> mapToItemDtos(Checklist checklist) {
        return checklist.getItems().stream()
                .map(item -> new ItemDto(
                        item.getId(),
                        item.getIsChecked(),
                        item.isDeleted(),
                        checklist.getId(),
                        item.getCreatedAt(),
                        item.getDeletedAt(),
                        item.getUpdatedAt(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public ChecklistDto createPredefinedChecklist(Long userId, CheckListType checkListType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Checklist checklist = Checklist.builder()
                .user(user)
                .title(checkListType.getTitle())
                .items(new ArrayList<>())
                .build();

        List<Item> items = checkListType.getItems().stream()
                .map(itemDescription -> Item.builder()
                        .description(itemDescription)
                        .isChecked(false)
                        .checklist(checklist)
                        .build())
                .collect(Collectors.toList());

        checklist.getItems().addAll(items);

        Checklist savedChecklist = checklistRepository.save(checklist);

        return new ChecklistDto(
                savedChecklist.getId(),
                savedChecklist.getUser().getId(),
                savedChecklist.getTitle(),
                savedChecklist.getItems().stream()
                        .map(item -> new ItemDto(
                                item.getId(),
                                item.getIsChecked(),
                                item.isDeleted(),
                                savedChecklist.getId(),
                                item.getCreatedAt(),
                                item.getDeletedAt(),
                                item.getUpdatedAt(),
                                item.getDescription()
                        ))
                        .collect(Collectors.toList()),
                savedChecklist.isDeleted()
        );
    }


    public Checklist createChecklist(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Checklist checklist = Checklist.builder()
                .user(user)
                .title(title)
                .build();
        System.out.println("checklist = " + checklist);
        return checklistRepository.save(checklist);
    }

    public ChecklistDto getChecklist(String userEmail, Long checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElse(null);

        if (checklist == null) {
            throw new RuntimeException("체크리스트를 못찾았어"); // 체크리스트가 존재하지 않는 경우
        }

        // 로그인한 사용자와 체크리스트 소유자 확인
        if (!checklist.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("이메일이 같지 않다"); // 권한이 없는 경우
        }

        return new ChecklistDto(
                checklist.getId(),
                checklist.getUser().getId(),
                checklist.getTitle(),
                checklist.getItems()
                        .stream()
                        .map(item -> new ItemDto(
                                item.getId(),
                                item.getIsChecked(),
                                item.isDeleted(),
                                checklist.getId(),
                                item.getCreatedAt(),
                                item.getDeletedAt(),
                                item.getUpdatedAt(),
                                item.getDescription()))
                        .toList(),
                checklist.isDeleted()
        );
    }

    public List<ChecklistDto> getChecklistsByUserId(String userEmail) {
        // 사용자 권한 검증
        User requestingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("요청한 사용자를 찾을 수 없습니다."));



        List<Checklist> checklists = checklistRepository.findAllByUserEmailAndUserIsDeletedFalseAndIsDeletedFalse(userEmail);
        List<ChecklistDto> checklistDtos = checklists.stream()
                .map(checklist -> new ChecklistDto(
                        checklist.getId(),
                        checklist.getUser().getId(),
                        checklist.getTitle(),
                        checklist.getItems()
                                .stream()
                                .map(item -> new ItemDto(
                                        item.getId(),
                                        item.getIsChecked(),
                                        item.isDeleted(),
                                        checklist.getId(),
                                        item.getCreatedAt(),
                                        item.getDeletedAt(),
                                        item.getUpdatedAt(),
                                        item.getDescription()))
                                .toList(),
                        checklist.isDeleted()
                )).toList();

        return checklistDtos;
    }


    @Transactional
    public ChecklistDto updateChecklist(String userEmail, Long checklistId, ChecklistUpdateDto request) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new NoSuchElementException("Checklist not found with id: " + checklistId));

        // 사용자 권한 검증
        if (!checklist.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You don't have permission to update this checklist");
        }

        Checklist updatedChecklist = Checklist.builder()
                .id(checklist.getId())
                .user(checklist.getUser())
                .title(request.title())
                .items(createUpdatedItems(request.items(), checklist))
                .build();

        updatedChecklist = checklistRepository.save(updatedChecklist);

        return new ChecklistDto(
                updatedChecklist.getId(),
                updatedChecklist.getUser().getId(),
                updatedChecklist.getTitle(),
                updatedChecklist.getItems().stream()
                        .map(this::convertToItemDto)
                        .toList(),
                updatedChecklist.isDeleted()
        );
    }

    private List<Item> createUpdatedItems(List<ItemUpdateDto> itemUpdateDtos, Checklist checklist) {
        if (itemUpdateDtos == null) {
            return new ArrayList<>();
        }
        return itemUpdateDtos.stream()
                .map(itemDto -> Item.builder()
                        .description(itemDto.description())
                        .isChecked(itemDto.isChecked())
                        .checklist(checklist)
                        .build())
                .toList();
    }

    private ItemDto convertToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getIsChecked(),
                item.isDeleted(),
                item.getChecklist().getId(),
                item.getCreatedAt(),
                item.getDeletedAt(),
                item.getUpdatedAt(),
                item.getDescription()
        );
    }

    public void deleteChecklist(Long checklistId) {
        checklistRepository.deleteById(checklistId);
    }
}
