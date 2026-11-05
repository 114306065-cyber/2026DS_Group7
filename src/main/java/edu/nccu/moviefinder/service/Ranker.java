package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.WebTree;
import java.util.List;
import org.springframework.stereotype.Service;

/** 依網站總分排序。 */
@Service
public class Ranker {

    /**
     * 以自己實作的 QuickSort,就地依 getTotalScore() 由高到低排序。
     * 不可呼叫 Collections.sort / List.sort。
     */
    public void sort(List<WebTree> trees) {
        // TODO(Member E, Stage 1)
        throw new UnsupportedOperationException("TODO(Member E): Ranker.sort");
    }
}
