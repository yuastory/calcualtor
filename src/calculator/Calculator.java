package calculator;

import java.util.ArrayList;
import java.util.List;

public class Calculator
{
    static class Tree
    {
        String     name = null;
        String     val  = "";
        List<Tree> sub  = new ArrayList<Tree>();

        public Tree(String name)
        {
            this.name = name;
        }

        public void add(Tree t)
        {
            sub.add(t);
        }
    }

    static void traverse(Tree t, int depth)
    {
        System.out.println(indent(depth) + t.name + ": " + t.val);
        for (int i = 0; i < t.sub.size(); i++)
            traverse(t.sub.get(i), depth + 1);
    }

    static String traverseCal(Tree t, int depth)
    {
        String result = "";
        for (int i = 0; i < t.sub.size(); i++)
            result = result + traverseCal(t.sub.get(i), depth + 1);
        if (result.length() != 0) result = calValInArr(split(result));
        return t.val + result;
    }

    public static String indent(int d)
    {
        String s = "";
        for (int i = 0; i < d; i++)
            s += i + 1 < d ? "  " : " + ";
        return s;
    }

    public static void main(String[] args)
    {
        String str = "-1000 + -10/-100 * +100 + -5"; // -995
        str = "1+(1+(2+1))";
        str = "(-999/-333)*(-222)+(-555)/(444)*+0.05-(-5)"; // -661.0625
        str = "(10-1)+10";
        str = "(10-1)+10+(10-5)";
        str = "(10+1) * 5 + 2 - 3 + (4 * 2 * (5 + 3)) + 2 * 6 + ( 9 + 2) - 60 + (3 + (20 - 1 * (6 + 4)) + 2 - 6) + 2";
        str = "3/(1+2)";
        str = "-(5+3) * 7 + -6*(7+8)";
        str = "+3-2";
        str = "(3+2)-(2-3)";
        str = " ( (((( ((((((10+-10+(((((10))))-1)))))))))))) ";
        str = "1+12/3*4-5+6*8/8-9*10";
        str = "(10+1)+7*((10/2)/(2+5))+4";
        str = "1+(5-(1+2)/(4/4))+5";
        str = "2(3+2)-5(4+2)";
        str = "(12+2)(12+2)";
        str = "-(5+2)-7(6+4)";
        str = "(5+5)/(5(1+4))";
        str = " ( 5     (       1 0 / 2   )  /  ( 5  + 5 ) ) ";
        str = "(5+5)/5(1+4)";
        str = "(((5+3)))((1)+4))";
        str = "(((5+3)))((1)+(4))";
        str = "-(3+2)(2+3)";
        str = "2(5+2)";
        str = "(5+2)(6+4)(7+2)+4";
        str = "((5+4)((2+3)(2+6))(3+2)";
        str = " (  2   *   (  3   +  4 )   )";
        System.out.println("값: " + calTree(str));
    }

    public static String calTree(String s)
    {
        Tree root = new Tree("root");
        ArrayList<String> arr = split(trim(s));
        s = "";
        for (String str : arr)
            s = s + str;
        makeTree(s, root, 1);
        traverse(root, 0);

        String[] sArr = traverseCal(root, 0).split("\\."); // 소숫점자리수 자르기
        return sArr[0] + (sArr[1].equals("0") ? "" : "." + sArr[1]);
    }

    public static int makeTree(String s, Tree node, int idx)
    {
        Tree sub = new Tree("sub" + idx++);
        node.add(sub);
        String str = "";
        for (int i = 0; i < s.length(); i++)
        {
            int tempI = i;
            if (s.charAt(i) == '(') i += makeTree(s.substring(i + 1), sub, idx) + 2;
            if (i >= s.length() || s.charAt(i) == ')')
            {
                if (!str.equals("")) sub.val = str;
                return i;
            }

            if (tempI != i)
            {
                sub.val = str;
                str = "";
                sub = new Tree("sub" + idx++);
                node.add(sub);
            }

            str += s.charAt(i);
        }

        if (!str.equals("")) sub.val = str;
        return node.val.length();
    }

    public static String calValInArr(ArrayList<String> arr)
    {
        String s = calValInArr0("*", "/", arr); // 곱&나 먼저 수행
        s = calValInArr0("+", "-", arr);
        return s;
    }

    public static String calValInArr0(String op1, String op2, ArrayList<String> arr)
    {
        if (arr.size() == 0) return "";
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i).equals(op1) || arr.get(i).equals(op2))
            {
                String sum = calculate(arr.remove(i - 1), arr.remove(i - 1), arr.remove(i - 1)); // 배열속 연산자 인덱스 좌우에 있는 피연산자들을 계산
                arr.add(i - 1, sum);
                i = 0;
            }
        }
        return arr.get(0);
    }

    public static ArrayList<String> split(String s)
    {
        ArrayList<String> arr = new ArrayList<String>();
        String str = ""; // 임시 저장공간 숫자를 담아둠.
        for (int i = 0; i < s.length(); i++) // 파싱을 시작함.
        {
            char c = s.charAt(i);
            switch (c)
            {
                case '+':
                case '-':
                    if (i == 0 || !isNum(s.charAt(i - 1)) && s.charAt(i - 1) != ')') // 숫자 음수일 경우 ex ) 5--5+4...
                    {
                        str += c;
                        break;
                    }
                case ')':
                case '(':
                    if (c == '(') // 오프닝 괄호와 왼쪽 캐릭터 사이에 연산자 없으면 곱하기로 간주함.
                    {
                        if (i != 0 && (isNum(s.charAt(i - 1)) || s.charAt(i - 1) == ')' || s.charAt(i - 1) == '-'))
                        {
                            str = (s.charAt(i - 1) == '-' ? str + "1" : str);
                            if (!str.equals("")) arr.add(str);
                            arr.add("*");
                            arr.add("(");
                            str = "";
                            break;
                        }
                    }
                case '*':
                case '/':
                    if (!str.equals("")) arr.add(str);
                    arr.add(String.valueOf(c));
                    str = "";
                    break;
                default: // 숫자, 점(dot)일 경우 str에 숫자 추가
                    str += c;
                    break;
            }
        }
        if (!str.equals("")) arr.add(str); // 마지막 숫자 배열에 추가
        return arr;
    }

    public static String calculate(String n1, String op, String n2)
    {
        double sum = 0;
        double dn1 = Double.parseDouble(n1);
        double dn2 = Double.parseDouble(n2);
        switch (op)
        {
            case "*":
                sum = dn1 * dn2;
                break;
            case "/":
                sum = dn1 / dn2;
                break;
            case "+":
                sum = dn1 + dn2;
                break;
            case "-":
                sum = dn1 - dn2;
                break;
        }
        return String.valueOf(sum);
    }

    public static String trim(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == ' ')
            {
                s = s.substring(0, i) + s.substring(i + 1);
                i--; // 삭제한 자리수부터 다시 탐색
            }
        }
        return s;
    }

    public static boolean isNum(char c)
    {
        return ((c >= '0' && c <= '9') ? true : false);
    }
}