//---------------------------------------------------------------------------
#ifndef mainH
#define mainH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <Dialogs.hpp>
#include <Menus.hpp>
#include <ExtCtrls.hpp>
#include <ComCtrls.hpp>
#include <Buttons.hpp>
#include <ActnList.hpp>
#include <ImgList.hpp>
#include <fstream.h>

#include "Molecules.h"
#include "FrameSeq.h"
#include "PrefWindow.h"
#include "MovieThread.h"
#include "CalcThread.h"
//---------------------------------------------------------------------------
class TMainForm : public TForm
{
__published:	// IDE-managed Components
   TMainMenu *MainMenu;
   TMenuItem *FileMenu;
   TMenuItem *OpenMenu;
   TMenuItem *ExitMenu;
   TOpenDialog *OpenDialog;
   TSaveDialog *SaveDialog;
   TButton *ProcessButton;
   TLabel *Label2;
   TEdit *DimEdit;
   TLabel *Label3;
   TShape *Bevel;
   TBevel *Bevel1;
   TMenuItem *LoadExMenu;
   TMenuItem *PrefMenu;
   TSpeedButton *PauseButton;
   TSpeedButton *StartButton;
   TSpeedButton *PrevButton;
   TSpeedButton *NextButton;
   TTrackBar *TrackBar;
   TEdit *CurEdit;
   TShape *ChooseBevel;
   TGroupBox *ROIGroupBox;
   TBevel *RectBevel;
   TLabel *RectLabel;
   TBevel *CursorBevel;
   TLabel *CursorLabel;
   TEdit *RightBottomEdit;
   TEdit *LeftTopEdit;
   TMenuItem *N1;
   TMenuItem *SaveMarksMenu;
   TMenuItem *N2;
   TButton *ExRoiButton;
   TButton *EmRoiButton;
   void __fastcall OpenMenuClick(TObject *Sender);
   void __fastcall ExitMenuClick(TObject *Sender);
   void __fastcall FormPaint(TObject *Sender);
   void __fastcall ProcessButtonClick(TObject *Sender);
   void __fastcall LoadExMenuClick(TObject *Sender);
   void __fastcall PrefMenuClick(TObject *Sender);
   void __fastcall BevelMouseDown(TObject *Sender, TMouseButton Button,
          TShiftState Shift, int X, int Y);
   void __fastcall BevelMouseMove(TObject *Sender, TShiftState Shift,
          int X, int Y);
   void __fastcall BevelMouseUp(TObject *Sender, TMouseButton Button,
          TShiftState Shift, int X, int Y);
   void __fastcall StartButtonClick(TObject *Sender);
   void __fastcall PauseButtonClick(TObject *Sender);
   void __fastcall PrevButtonClick(TObject *Sender);
   void __fastcall NextButtonClick(TObject *Sender);
   void __fastcall TrackBarChange(TObject *Sender);
   void __fastcall FormResize(TObject *Sender);
   void __fastcall SaveMarksMenuClick(TObject *Sender);
   void __fastcall ExRoiButtonClick(TObject *Sender);
   void __fastcall EmRoiButtonClick(TObject *Sender);
private:	// User declarations
//   TColor PixColor(float);
   PPars Pars;
   Graphics::TBitmap *Bitmap;
   FrameSeq Movie;
   Frame ExFrame;
   AnsiString FName,CurDir;
   TRect rect;
   MolecListArray Results;
   void LoadMovie(int);
   void DrawBitmap(Frame&);
   void DrawImage();
   void SetROI();
public:		// User declarations
   __fastcall TMainForm(TComponent* Owner);
   __fastcall ~TMainForm();
   MovieThread* MovThread;
   bool MovOn;
   CalcThread* CalThread;
   bool CalOn;
   float *ExSignals,*EmSignals;
   void ListResults();
friend class CalcThread;
};
//---------------------------------------------------------------------------
extern PACKAGE TMainForm *MainForm;
//---------------------------------------------------------------------------
#endif
