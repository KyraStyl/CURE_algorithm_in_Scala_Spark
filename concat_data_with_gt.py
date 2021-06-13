#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jun  8 11:36:00 2021

@author: xteloulou
"""


file1 = "data/datasets/data_2/data1.txt"
file2 = "correctness/ground_truth_big.txt"
file3 = "correctness/ground_truth_full_big.txt"

with open(file1,"r") as f1:
    with open(file2, "r") as f2:
        with open(file3, "w") as w:
            for x,y in zip(f1,f2):
                w.write(x.strip()+","+y.strip()+"\n")