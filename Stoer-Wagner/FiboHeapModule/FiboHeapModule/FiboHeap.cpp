#include "pybind11/pybind11.h"

#include <cmath>
#include <vector>
#include <stdexcept>
#include <climits>

template<typename KeyType>
struct FiboNode {
    KeyType key;
    int degree;
    // whether it is cutted.
    bool marked;

    FiboNode* parent;
    FiboNode* child;
    FiboNode* left;
    FiboNode* right;

    FiboNode(const KeyType& key_)
        : key(key_), degree(0), marked(false),
        parent(nullptr), child(nullptr), left(this), right(this) {
        ;
    }
};

template<typename KeyType>
class FiboHeap {
private:
    // we ONLY care about the minimal node!!
    FiboNode<KeyType>* minNode;
    // th total count of node.
    int nodeCount;

    void insertIntoRootList(FiboNode<KeyType>* node);
    void removeFromRootList(FiboNode<KeyType>* node);
    // merge a tree into another tree.
    void linkTree(FiboNode<KeyType>* y, FiboNode<KeyType>* x);
    // merge the tree with SAME degree.
    void consolidate();
    // remove node in parent's children.
    void cut(FiboNode<KeyType>* node, FiboNode<KeyType>* parent);
    void cascadingCut(FiboNode<KeyType>* node);

public:
    FiboHeap() : minNode(nullptr), nodeCount(0) {
        ;
    }

    FiboNode<KeyType>* insert(const KeyType& key);
    KeyType getMin() const;
    bool empty() const;

    KeyType extractMin();
    void decreaseKey(FiboNode<KeyType>* node, const KeyType& newKey);
    void deleteNode(FiboNode<KeyType>* node, const KeyType& minKeyValue);
};

template<typename KeyType>
void FiboHeap<KeyType>::insertIntoRootList(FiboNode<KeyType>* node) {
    if (minNode == nullptr) {
        minNode = node;
    }
    else {
        // insert the node in the RIGHT side of min.
        node->left = minNode;
        node->right = minNode->right;
        minNode->right->left = node;
        minNode->right = node;

        // update min node if necessary.
        if (node->key < minNode->key) {
            minNode = node;
        }
    }
}

template<typename KeyType>
void FiboHeap<KeyType>::removeFromRootList(FiboNode<KeyType>* node) {
    node->left->right = node->right;
    node->right->left = node->left;
    // avoid dangling.
    node->left = node->right = node;
}

// merge a tree y into another tree x.
// caution: when doing this, we will remove y form root linked list.
template<typename KeyType>
void FiboHeap<KeyType>::linkTree(FiboNode<KeyType>* y, FiboNode<KeyType>* x) {
    removeFromRootList(y);

    // y will be the child of x.
    y->parent = x;
    y->left = y->right = y;

    // tricks: we don't need a vector<FiboNode*> in FiboNode to contains
    // the children of it, since the children can ALSO be a double-cycled linked list!!
    if (!x->child) {
        x->child = y;
    }
    else {
        y->right = x->child->right;
        y->left = x->child;
        x->child->right->left = y;
        x->child->right = y;
    }

    x->degree += 1;
    y->marked = false;
}

// merge the tree with SAME degree. each degree will contain no more than
// 1 tree.
template<typename KeyType>
void FiboHeap<KeyType>::consolidate() {
    const int maxDegree = 64;
    std::vector<FiboNode<KeyType>*> A(maxDegree, nullptr);

    std::vector<FiboNode<KeyType>*> roots;
    FiboNode<KeyType>* curr = minNode;
    if (!curr) return;

    do {
        roots.push_back(curr);
        curr = curr->right;
    } while (curr != minNode);

    for (FiboNode<KeyType>* w : roots) {
        FiboNode<KeyType>* x = w;
        int d = x->degree;

        while (A[d]) {
            FiboNode<KeyType>* y = A[d];
            // make sure the 
            if (y->key < x->key) {
                std::swap(x, y);
            }
            linkTree(y, x);
            A[d] = nullptr;
            d += 1;
        }

        A[d] = x;
    }

    minNode = nullptr;
    for (FiboNode<KeyType>* node : A) {
        if (node) {
            node->left = node->right = node;
            node->parent = nullptr;
            insertIntoRootList(node);
        }
    }
}

// remove node from parent's child.
template<typename KeyType>
void FiboHeap<KeyType>::cut(FiboNode<KeyType>* node, FiboNode<KeyType>* parent) {
    if (node->right == node) {
        parent->child = nullptr;
    }
    else {
        if (parent->child == node) {
            parent->child = node->right;
        }
        node->left->right = node->right;
        node->right->left = node->left;
    }

    parent->degree--;
    node->parent = nullptr;
    node->left = node->right = node;
    node->marked = false;

    insertIntoRootList(node);
}

template<typename KeyType>
void FiboHeap<KeyType>::cascadingCut(FiboNode<KeyType>* node) {
    FiboNode<KeyType>* parent = node->parent;
    if (!parent) return;

    if (!node->marked) {
        node->marked = true;
    }
    else {
        cut(node, parent);
        cascadingCut(parent);
    }
}

template<typename KeyType>
FiboNode<KeyType>* FiboHeap<KeyType>::insert(const KeyType& key) {
    FiboNode<KeyType>* node = new FiboNode<KeyType>(key);
    insertIntoRootList(node);
    ++nodeCount;
    return node;
}

template<typename KeyType>
KeyType FiboHeap<KeyType>::getMin() const {
    if (minNode == nullptr) {
        throw std::runtime_error("Heap is empty");
    }
    return minNode->key;
}

template<typename KeyType>
bool FiboHeap<KeyType>::empty() const {
    return minNode == nullptr;
}

template<typename KeyType>
KeyType FiboHeap<KeyType>::extractMin() {
    if (!minNode) throw std::runtime_error("Heap is empty");

    FiboNode<KeyType>* oldMin = minNode;

    // step1: put all children node of oldMin into root linked list.
    if (oldMin->child) {
        FiboNode<KeyType>* child = oldMin->child;
        FiboNode<KeyType>* curr = child;
        do {
            FiboNode<KeyType>* next = curr->right;

            // break the old parent.
            curr->parent = nullptr;
            insertIntoRootList(curr);

            curr = next;
        } while (curr != child);
    }

    // step2: remove minNode form root linked list.
    if (oldMin == oldMin->right) {
        // it is the LAST node.
        minNode = nullptr;
    }
    else {
        minNode = oldMin->right;
        removeFromRootList(oldMin);
        // step3: consolidate.
        consolidate();
    }

    --nodeCount;
    KeyType minKey = oldMin->key;
    delete oldMin;
    return minKey;
}

template<typename KeyType>
void FiboHeap<KeyType>::decreaseKey(FiboNode<KeyType>* node, const KeyType& newKey) {
    if (!node) return;

    if (newKey > node->key) {
        throw std::invalid_argument("New key is greater than current key");
    }

    node->key = newKey;
    FiboNode<KeyType>* parent = node->parent;

    if (parent && node->key < parent->key) {
        cut(node, parent);
        cascadingCut(parent);
    }

    if (node->key < minNode->key) {
        minNode = node;
    }
}

// caution: the user must define the MININUM value of KeyType to make this work.
template<typename KeyType>
void FiboHeap<KeyType>::deleteNode(FiboNode<KeyType>* node, const KeyType& minKeyValue) {
    decreaseKey(node, minKeyValue);
    extractMin();
}

namespace py = pybind11;

PYBIND11_MODULE(FiboHeapModule, m) {
    py::class_<FiboNode<int>>(m, "FiboNode");

    py::class_<FiboHeap<int>>(m, "FiboHeap")
        .def(py::init<>())
        .def("insert", &FiboHeap<int>::insert, py::return_value_policy::reference)
        .def("extract_min", &FiboHeap<int>::extractMin)
        .def("get_min", &FiboHeap<int>::getMin)
        .def("empty", &FiboHeap<int>::empty)
        .def("decrease_key", &FiboHeap<int>::decreaseKey);
}