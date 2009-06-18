
#define TWIRL_SPEED_SLOW        100
#define TWIRL_SPEED_FAST        20

#define MENUS_Y                 32
#define MAINMENU_X              32

#define STORYBOARD_X			672
#define STORYBOARD_Y			32

#define FONT_TWIRL              9
#define TWIRL_SPEED             30
#define TWIRL_NUM_FRAMES        5

#include "../CGame.h"

char controlsmenu(stCloneKeenPlus *pCKP);
int mainmenu(stCloneKeenPlus *pCKP,int defaultopt);
char configmenu(stCloneKeenPlus *pCKP);
short loadStartMenu(stCloneKeenPlus *pCKP);
void keensleft(stCloneKeenPlus *pCKP);
int intro(stCloneKeenPlus *pCKP);
void showmapatpos(int level, int xoff, int yoff, int wm, stCloneKeenPlus *pCKP);
short loadResourcesforStartMenu(stCloneKeenPlus *pCKP, CGame *Game);
int getDifficulty(stCloneKeenPlus *pCKP);
void showPage(char *text, stCloneKeenPlus *pCKP, int textsize);