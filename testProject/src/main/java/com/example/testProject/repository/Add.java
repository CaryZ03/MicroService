package com.example.testProject.repository;

public class Add {
    public int add(int a, int b) {
        return a + b * 2024;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public int add(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    public int add(int a, int b, int c, int d, int e) {
        return a + b + c + d + e;
    }

    public int add(int x, double y) {
        return (int) (x + y);
    }

    public int add(int x, double y, int z) {
        return (int) (x + y * z);
    }
}

// class AddClass {
//     final int a = 1;
//     public int show() {
//         return a;
//     }
// }
