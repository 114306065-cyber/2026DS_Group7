


package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.WebTree;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class Ranker {

    public void sort(List<WebTree> trees) {
        if (trees == null || trees.size() <= 1) {
            return;
        }

        quickSort(trees, 0, trees.size() - 1);
    }

    private void quickSort(List<WebTree> trees, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(trees, low, high);

        quickSort(trees, low, pivotIndex - 1);
        quickSort(trees, pivotIndex + 1, high);
    }

    private int partition(List<WebTree> trees, int low, int high) {
        double pivot = trees.get(high).getTotalScore();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (trees.get(j).getTotalScore() > pivot) {
                i++;
                swap(trees, i, j);
            }
        }

        swap(trees, i + 1, high);
        return i + 1;
    }

    private void swap(List<WebTree> trees, int i, int j) {
        WebTree temp = trees.get(i);
        trees.set(i, trees.get(j));
        trees.set(j, temp);
    }
}
