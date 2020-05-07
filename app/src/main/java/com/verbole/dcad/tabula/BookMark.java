package com.verbole.dcad.tabula;

public class BookMark implements Comparable<BookMark> {
    // pref : auteur, oeuvre, partie, chapitre, scrollY (int)
    int indAuteur = 0;
    int indPartie = 0;
    int indChapitre = 0;
    int scrollY = -1;
    String orientation = "P";

    public BookMark(int indAuteur, int indPartie, int indChapitre, int scrollY) {
        this.indAuteur = indAuteur;
        this.indPartie = indPartie;
        this.indChapitre = indChapitre;
        this.scrollY = scrollY;
        this.orientation = "P";
    }


    @Override
    public int compareTo(BookMark o) {
        if (o.indAuteur < this.indAuteur) {
            return 1;
        }
        if (o.indAuteur > this.indAuteur) {
            return -1;
        }
        return Integer.compare(this.indPartie, o.indPartie);
    }
}
