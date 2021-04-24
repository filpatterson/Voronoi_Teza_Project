package HalfPlaneIntersectionMethod;

class QuickSort {
    /**
     * swap to Site elements
     * @param arr array of Site elements
     * @param i first to swap
     * @param j second to swap
     */
    static void swap(Site[] arr, int i, int j) {
        Site temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * take last element as pivot, place smaller to the left from pivot and greater to the right of pivot
     * @param arr array of Sites
     * @param low lowest index of given segment
     * @param high highest index of given segment
     * @return partition index
     */
    static int partition(Site[] arr, int low, int high) {
        // pivot
        Site pivot = arr[high];

        //  smaller element index, indicates right position of pivot so far
        int i = (low - 1);

        for(int j = low; j <= high - 1; j++) {
            //  if current element is smaller than pivot
            if (arr[j].getWeight() < pivot.getWeight()) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return (i + 1);
    }

    /**
     * perform quick sort for given array of sites, comparing by their "weight"
     * @param arr array of sites
     * @param low lowest index of segment requiring sorting
     * @param high highest index of segment requiring sorting
     */
    static void performSort(Site[] arr, int low, int high) {
        if (low < high) {

            // pi is partitioning index, arr[p]
            // is now at right place
            int pi = partition(arr, low, high);

            // Separately sort elements before
            // partition and after partition
            performSort(arr, low, pi - 1);
            performSort(arr, pi + 1, high);
        }
    }
}
