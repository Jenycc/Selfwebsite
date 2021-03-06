package cn.edu.sicnu.cs.yuxin.exp2;

import java.util.Scanner;

public class StatementCheck {
    public static void main(String[] args) {
        String str;
        Scanner input = new Scanner(System.in);
        System.out.println("请输入句子：");
        str = input.nextLine();
        System.out.println(toChange(str));
        input.close();
    }

    public static String toChange(String str) { //英语语句格式化函数，每个单词首字母大写，返回转换后的字符串
        char[] charray = str.toCharArray();
        if (!(Character.isLowerCase(charray[0]) || Character.isUpperCase(charray[0]))) {
            return "输入错误！";
        }
        StringBuilder strbuff = new StringBuilder().append(Character.toUpperCase(charray[0]));
        for (int i = 1; i < str.length() - 1; i++) {
            if (!Character.isAlphabetic(charray[i]) && Character.isAlphabetic(charray[i + 1])) {    //判断是否是单词首字母
                strbuff.append(charray[i]);
                if (charray[i] == '\'' || charray[i] == '-') {  //判断单词是否是连词
                    strbuff.append(charray[i + 1]);
                } else {
                    strbuff.append(Character.toUpperCase(charray[i + 1]));
                }
                i++;
            } else {
                strbuff.append(Character.toLowerCase(charray[i]));
            }
        }
        strbuff.append(charray[str.length() - 1]);
        return strbuff.toString();
    }
}
