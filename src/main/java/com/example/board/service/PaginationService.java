package com.example.board.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);  // 현재 페이지 번호에서 전체 길이의 절반만큼을 빼서 페이지 시작 번호를 구한다.
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);  // 페이지 시작 번호에 페이지네이션 바의 길이를 더해서 페이지 끝 번호를 구한다.

        return IntStream.range(startNumber, endNumber).boxed().toList();  // 페이지 번호 범위를 primitive int 배열로 만들고, 이것을 박싱해서 리스트로 만든다.
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
