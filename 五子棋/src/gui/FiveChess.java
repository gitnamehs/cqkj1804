package gui;

public class FiveChess
{
    public int[][] coord ;
    
   

    public boolean iswin(int x, int y, int[][] arr)
    {
        //把下棋的字传给coord,
        coord=arr;
        if (iswinr(x, y) || iswinrr(x, y) || iswinw(x, y) || iswinww(x, y))
        {
          return true;
        } else
        {
            return false;
        }

    }

    // 判定横向的，改变的是y的值
    private boolean iswinr(int x, int y)
    {
        int num = 0;
        int i = y;
        while (i <19)
        {
            if (coord[x][i] ==coord[x][y] )
            {
                ++num;
                i++;

            } else
            {
                break;
            }
        }
        while (num < 5 && y >= 0)
        {
            if (coord[x][y-1] ==coord[x][y]  )
            {
                num++;
                y--;

            } else
            {
                break;
            }
        }
        return num  >= 5 ? true : false;

    }

    // 判定纵向的
    // y不变，x改变
    private boolean iswinrr(int x, int y)
    {

        int num = 0;
        int i = x;
        while (i <19)
        {
            if (coord[i][y] ==coord[x][y] )
            {
                ++num;
                i++;
            } else
            {
                break;
            }
        }
        while (num < 5 && x >= 0)
        {
            if (coord[x-1][y] == coord[x][y])
            {
                num++;
                x--;

            } else
            {
                break;
            }
        }

        return num >= 5 ? true : false;

    }

    // 判定左斜的
    private boolean iswinw(int x, int y)
    {

        int num = 0;
        int i = x;
        int j = y;
        while (i <19 && j >= 0)
        {
            if (coord[i][j] ==coord[x][y] )
            {
                ++num;
                i++;
                j--;
            } else
            {
                break;
            }
        }
        while (num < 5 && x >= 0 && y <19)
        {
            if (coord[x-1][y+1] ==coord[x][y] )
            {
                num++;
                x--;
                y++;

            } else
            {
                break;
            }
        }
        return num >= 5 ? true : false;

    }

    // 判定右斜的
    private boolean iswinww(int x, int y)
    {

        int num = 0;
        int i = x;
        int j = y;
        while (i >= 0 && j >= 0)
        {
            if (coord[i][j] == coord[x][y])
            {
                ++num;
                i--;
                j--;
            } else
            {
                break;
            }
        }
        while (num < 5 && x <19 && y <19)
        {
            if (coord[x+1][y+1] ==coord[x][y] )
            {
                num++;
                x++;
                y++;

            } else
            {
                break;
            }
        }
        return num  >= 5 ? true : false;

    }

}
