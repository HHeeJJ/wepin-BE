package wepin.store.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CommonPage<T> {
    
    private long    totalCount;
    private long    pageNumber;
    private boolean hasBack;
    private boolean hasNext;
    private List<T> lists;
    private T       detail;

}
