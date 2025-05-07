import FiboHeapModule as FHM

def test_fibo_heap():
    # Create a Fibonacci heap
    fibo_heap = FHM.FiboHeap()

    # Insert elements into the Fibonacci heap
    fibo_heap.insert(10)
    fibo_heap.insert(20)
    fibo_heap.insert(5)

    # Check the minimum element
    assert fibo_heap.get_min() == 5, "Minimum should be 5"

    # Extract the minimum element
    min_elem = fibo_heap.extract_min()
    assert min_elem == 5, "Extracted minimum should be 5"
    assert fibo_heap.get_min() == 10, "New minimum should be 10 after extraction"

    # Decrease key
    fibo_heap.decrease_key(20, 8)
    assert fibo_heap.get_min() == 8, "Minimum should be 8 after decrease key"