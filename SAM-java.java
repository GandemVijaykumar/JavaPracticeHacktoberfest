import java.util.*;

public class SuffixAutomaton {
    private static class State {
        int len, link;
        Map<Character,Integer> next = new HashMap<>();
        long occ;
    }

    private final ArrayList<State> st = new ArrayList<>();
    private int last;

    public SuffixAutomaton(String s) {
        st.add(new State());
        st.get(0).len = 0;
        st.get(0).link = -1;
        last = 0;
        for (char c : s.toCharArray()) extend(c);
        computeOccurrences();
    }

    private void extend(char c) {
        int cur = st.size();
        st.add(new State());
        st.get(cur).len = st.get(last).len + 1;
        st.get(cur).occ = 1;
        int p = last;
        while (p != -1 && !st.get(p).next.containsKey(c)) {
            st.get(p).next.put(c, cur);
            p = st.get(p).link;
        }
        if (p == -1) {
            st.get(cur).link = 0;
        } else {
            int q = st.get(p).next.get(c);
            if (st.get(p).len + 1 == st.get(q).len) {
                st.get(cur).link = q;
            } else {
                int clone = st.size();
                st.add(new State());
                State sq = st.get(q);
                State sc = st.get(clone);
                sc.len = st.get(p).len + 1;
                sc.next.putAll(sq.next);
                sc.link = sq.link;
                sc.occ = 0;
                while (p != -1 && st.get(p).next.get(c) == q) {
                    st.get(p).next.put(c, clone);
                    p = st.get(p).link;
                }
                sq.link = sc.link = clone;
                st.get(cur).link = clone;
            }
        }
        last = cur;
    }

    private void computeOccurrences() {
        int maxLen = 0;
        for (State s : st) if (s.len > maxLen) maxLen = s.len;
        int[] cnt = new int[maxLen + 1];
        for (State s : st) cnt[s.len]++;
        for (int i = 1; i <= maxLen; i++) cnt[i] += cnt[i - 1];
        int[] order = new int[st.size()];
        for (int i = st.size() - 1; i >= 0; i--) order[--cnt[st.get(i).len]] = i;
        for (int i = st.size() - 1; i > 0; i--) {
            int v = order[i];
            int p = st.get(v).link;
            if (p >= 0) st.get(p).occ += st.get(v).occ;
        }
    }

    public long countDistinctSubstrings() {
        long res = 0;
        for (State s : st) {
            int l = s.len;
            int pl = s.link == -1 ? 0 : st.get(s.link).len;
            res += (l - pl);
        }
        return res;
    }

    public String longestCommonSubstring(String t) {
        int v = 0, l = 0, best = 0, bestpos = 0;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (st.get(v).next.containsKey(c)) {
                v = st.get(v).next.get(c);
                l++;
            } else {
                while (v != -1 && !st.get(v).next.containsKey(c)) v = st.get(v).link;
                if (v == -1) { v = 0; l = 0; }
                else { l = st.get(v).len + 1; v = st.get(v).next.get(c); }
            }
            if (l > best) { best = l; bestpos = i; }
        }
        return t.substring(bestpos - best + 1, bestpos + 1);
    }

    public long occurrences(String p) {
        int v = 0;
        for (char c : p.toCharArray()) {
            if (!st.get(v).next.containsKey(c)) return 0;
            v = st.get(v).next.get(c);
        }
        return st.get(v).occ;
    }

    public static void main(String[] args) {
        String s = "ababa";
        SuffixAutomaton sa = new SuffixAutomaton(s);
        System.out.println("Distinct substrings: " + sa.countDistinctSubstrings());
        System.out.println("Occur 'aba': " + sa.occurrences("aba"));
        System.out.println("LCS with 'babab': " + sa.longestCommonSubstring("babab"));
    }
}
