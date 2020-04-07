package com.fun.framework.utils;

import com.fun.api.domain.FxFriends;
import com.fun.api.domain.MyList;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class listToSortByName {

    public static List<MyList> listToSortByName(List<FxFriends> list) {
        List<MyList> list2 = new ArrayList<>();
        MyList a = new MyList();
        a.setLetter("A");
        MyList b = new MyList();
        b.setLetter("B");
        MyList c = new MyList();
        c.setLetter("C");
        MyList d = new MyList();
        d.setLetter("D");
        MyList e = new MyList();
        e.setLetter("E");
        MyList f = new MyList();
        f.setLetter("F");
        MyList g = new MyList();
        g.setLetter("G");
        MyList h = new MyList();
        h.setLetter("H");
        MyList ii = new MyList();
        ii.setLetter("I");
        MyList j = new MyList();
        j.setLetter("J");
        MyList k = new MyList();
        k.setLetter("K");
        MyList l = new MyList();
        l.setLetter("L");
        MyList m = new MyList();
        m.setLetter("M");
        MyList n = new MyList();
        n.setLetter("N");
        MyList o = new MyList();
        o.setLetter("O");
        MyList p = new MyList();
        p.setLetter("P");
        MyList q = new MyList();
        q.setLetter("Q");
        MyList r = new MyList();
        r.setLetter("R");
        MyList s = new MyList();
        s.setLetter("S");
        MyList t = new MyList();
        t.setLetter("T");
        MyList u = new MyList();
        u.setLetter("U");
        MyList v = new MyList();
        v.setLetter("V");
        MyList w = new MyList();
        w.setLetter("W");
        MyList x = new MyList();
        x.setLetter("X");
        MyList y = new MyList();
        y.setLetter("Y");
        MyList z = new MyList();
        z.setLetter("Z");
        MyList jin = new MyList();
        jin.setLetter("#");
        List<FxFriends> lista = new ArrayList<>();
        List<FxFriends> listb = new ArrayList<>();
        List<FxFriends> listc = new ArrayList<>();
        List<FxFriends> listd = new ArrayList<>();
        List<FxFriends> liste = new ArrayList<>();
        List<FxFriends> listf = new ArrayList<>();
        List<FxFriends> listg = new ArrayList<>();
        List<FxFriends> listh = new ArrayList<>();
        List<FxFriends> listi = new ArrayList<>();
        List<FxFriends> listj = new ArrayList<>();
        List<FxFriends> listk = new ArrayList<>();
        List<FxFriends> listl = new ArrayList<>();
        List<FxFriends> listm = new ArrayList<>();
        List<FxFriends> listn = new ArrayList<>();
        List<FxFriends> listo = new ArrayList<>();
        List<FxFriends> listp = new ArrayList<>();
        List<FxFriends> listq = new ArrayList<>();
        List<FxFriends> listr = new ArrayList<>();
        List<FxFriends> lists = new ArrayList<>();
        List<FxFriends> listt = new ArrayList<>();
        List<FxFriends> listu = new ArrayList<>();
        List<FxFriends> listv = new ArrayList<>();
        List<FxFriends> listw = new ArrayList<>();
        List<FxFriends> listx = new ArrayList<>();
        List<FxFriends> listy = new ArrayList<>();
        List<FxFriends> listz = new ArrayList<>();
        List<FxFriends> listjin = new ArrayList<>();
//        Map<Map<String, String>, Map<String, List<FxFriends>>> map = new HashMap<>();
//        Map<String, String> mapa = new HashMap<>();
//        mapa.put("letter","A");
//        map.put(mapa);
//        List<MyList> myLists = new ArrayList<>();
//
//
        String names[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getFriendRemark();
            String alphabet = name.substring(0, 1);
            /*判断首字符是否为中文*/
            if (alphabet.matches("[\\u4e00-\\u9fa5]")) {
                switch (getAlphabet(name)) {
                    case "a":
                        lista.add(list.get(i));
                        break;
                    case "b":
                        listb.add(list.get(i));
                        break;
                    case "c":
                        listc.add(list.get(i));
                        break;
                    case "d":
                        listd.add(list.get(i));
                        break;
                    case "e":
                        liste.add(list.get(i));
                        break;
                    case "f":
                        listf.add(list.get(i));
                        break;
                    case "g":
                        listg.add(list.get(i));
                        break;
                    case "h":
                        listh.add(list.get(i));
                        break;
                    case "i":
                        listi.add(list.get(i));
                        break;
                    case "j":
                        listj.add(list.get(i));
                        break;
                    case "k":
                        listk.add(list.get(i));
                        break;
                    case "l":
                        listl.add(list.get(i));
                        break;
                    case "m":
                        listm.add(list.get(i));
                        break;
                    case "n":
                        listn.add(list.get(i));
                        break;
                    case "o":
                        listo.add(list.get(i));
                        break;
                    case "p":
                        listp.add(list.get(i));
                        break;
                    case "q":
                        listq.add(list.get(i));
                        break;
                    case "r":
                        listr.add(list.get(i));
                        break;
                    case "s":
                        lists.add(list.get(i));
                        break;
                    case "t":
                        listt.add(list.get(i));
                        break;
                    case "u":
                        listu.add(list.get(i));
                        break;
                    case "v":
                        listv.add(list.get(i));
                        break;
                    case "w":
                        listw.add(list.get(i));
                        break;
                    case "x":
                        listx.add(list.get(i));
                        break;
                    case "y":
                        listy.add(list.get(i));
                        break;
                    case "z":
                        listz.add(list.get(i));
                        break;
                    default:
                        listjin.add(list.get(i));
                }
            } else {
                switch (alphabet) {
                    case "a":
                        lista.add(list.get(i));
                        break;
                    case "b":
                        listb.add(list.get(i));
                        break;
                    case "c":
                        listc.add(list.get(i));
                        break;
                    case "d":
                        listd.add(list.get(i));
                        break;
                    case "e":
                        liste.add(list.get(i));
                        break;
                    case "f":
                        listf.add(list.get(i));
                        break;
                    case "g":
                        listg.add(list.get(i));
                        break;
                    case "h":
                        listh.add(list.get(i));
                        break;
                    case "i":
                        listi.add(list.get(i));
                        break;
                    case "j":
                        listj.add(list.get(i));
                        break;
                    case "k":
                        listk.add(list.get(i));
                        break;
                    case "l":
                        listl.add(list.get(i));
                        break;
                    case "m":
                        listm.add(list.get(i));
                        break;
                    case "n":
                        listn.add(list.get(i));
                        break;
                    case "o":
                        listo.add(list.get(i));
                        break;
                    case "p":
                        listp.add(list.get(i));
                        break;
                    case "q":
                        listq.add(list.get(i));
                        break;
                    case "r":
                        listr.add(list.get(i));
                        break;
                    case "s":
                        lists.add(list.get(i));
                        break;
                    case "t":
                        listt.add(list.get(i));
                        break;
                    case "u":
                        listu.add(list.get(i));
                        break;
                    case "v":
                        listv.add(list.get(i));
                        break;
                    case "w":
                        listw.add(list.get(i));
                        break;
                    case "x":
                        listx.add(list.get(i));
                        break;
                    case "y":
                        listy.add(list.get(i));
                        break;
                    case "z":
                        listz.add(list.get(i));
                        break;
                    default:
                        listjin.add(list.get(i));
                }
            }
        }
        a.setData(lista);
        b.setData(listb);
        c.setData(listc);
        d.setData(listd);
        e.setData(liste);
        f.setData(listf);
        g.setData(listg);
        h.setData(listh);
        ii.setData(listi);
        j.setData(listj);
        k.setData(listk);
        l.setData(listl);
        m.setData(listm);
        n.setData(listn);
        o.setData(listo);
        p.setData(listp);
        q.setData(listq);
        r.setData(listr);
        s.setData(lists);
        t.setData(listt);
        u.setData(listu);
        v.setData(listv);
        w.setData(listw);
        x.setData(listx);
        y.setData(listy);
        z.setData(listz);
        jin.setData(listjin);
        list2.add(a);
        list2.add(b);
        list2.add(c);
        list2.add(d);
        list2.add(e);
        list2.add(f);
        list2.add(g);
        list2.add(h);
        list2.add(ii);
        list2.add(j);
        list2.add(k);
        list2.add(l);
        list2.add(m);
        list2.add(n);
        list2.add(o);
        list2.add(p);
        list2.add(q);
        list2.add(r);
        list2.add(s);
        list2.add(t);
        list2.add(u);
        list2.add(v);
        list2.add(w);
        list2.add(x);
        list2.add(y);
        list2.add(z);
        list2.add(jin);
        //ListIterator<MyList> it = list2.listIterator();
        Iterator<MyList> it = list2.iterator();
        while(it.hasNext()){
            MyList next = it.next();
            if(next.getData().size()==0){
                it.remove();
            }
        }
        return list2;
    }
    public static String getAlphabet(String str) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出拼音全部小写
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不带声调
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0), defaultFormat)[0];
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return pinyin.substring(0, 1);
    }
}
