package com.mini.boot.demo;

import com.mini.boot.annotation.AutoConfiguration;
import com.mini.boot.context.annotation.ImportCandidates;

public class ImportCandidatesDemo {
    public static void main(String[] args){
        ImportCandidates candidates = ImportCandidates.load(AutoConfiguration.class,ImportCandidates.class.getClassLoader());
        System.out.println(candidates.getCandidates());
    }
}
