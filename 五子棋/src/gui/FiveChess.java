package gui;

public class FiveChess
{
    public int[][] coord ;
    
   

    public boolean iswin(int x, int y, int[][] arr)
    {
        //��������ִ���coord,
        coord=arr;
        if (iswinr(x, y) || iswinrr(x, y) || iswinw(x, y) || iswinww(x, y))
        {
          return true;
        } else
        {
            return false;
        }

    }

    // �ж�����ģ��ı����y��ֵ
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

    // �ж������
    // y���䣬x�ı�
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

    // �ж���б��
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

    // �ж���б��
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
