#ifndef MoleculesH
#define MoleculesH

#include <Classes.hpp>
#include <iostream.h>
#include <vector.h>
//-----------------------------------------------------------------
class MolecList;
class Molecule
{
public:
   Molecule();
   Molecule(const Molecule&);
   Molecule(int,int,float);
   inline ~Molecule(){};
   Molecule& operator=(const Molecule&);
   int x,y;
   float I;
   bool operator==(const Molecule&); //coordinates only
   bool operator!=(const Molecule&); //coordinates only
   friend AnsiString& operator<<(AnsiString&,Molecule&);
   friend bool operator<(const Molecule&,const Molecule&);
   friend bool operator>(const Molecule&,const Molecule&);
   friend bool operator<=(const Molecule&,const Molecule&);
   friend bool operator>=(const Molecule&,const Molecule&);
   friend float operator-(const Molecule&,const Molecule&);//difference between Y only
   friend float Dist(const Molecule&,const Molecule&);
   friend int __fastcall MolCompare(void*,void*);
   friend ostream& operator<<(ostream&,Molecule&);
   friend istream& operator>>(istream&,Molecule&);
   friend class MolecList;
};
//-----------------------------------------------------------------
class MoleculeExInt:public Molecule
{
public:
   float ExInt;
};
//-----------------------------------------------------------------
class MolecList:public TList
{
public:
   inline __fastcall MolecList(void){};
   __fastcall MolecList(int);
   __fastcall MolecList(MolecList*);
   __fastcall ~MolecList(void);
//   MolecList& operator=(MolecList*);
   Molecule& Mol(int);
   int __fastcall Add(Molecule&);
   void __fastcall Clear(void);
   void __fastcall Delete(int);
   friend TStrings* operator<<(TStrings*,MolecList&);
   friend istream& operator>>(istream&,MolecList&);
};
//-----------------------------------------------------------------
class MolecListArray
{
public:
   MolecListArray();
   MolecListArray(int);
   MolecListArray(const MolecListArray&);
   ~MolecListArray(void);
   MolecListArray& operator=(const MolecListArray&);
   int AddList(MolecList*,int);
   void SetNumberOfLists(int);
   void DeleteMolecule(int);
   MolecList* operator[](int);
   int GetN(){return N;};
private:
   MolecList** Lists;
   int N;
};
//-----------------------------------------------------------------
class MolecIntList:public TList
{
public:
   __fastcall ~MolecIntList(void);
friend TStrings* operator<<(TStrings*,MolecIntList&);
};  
//-----------------------------------------------------------------
AnsiString& operator<<(AnsiString&,Molecule&);
bool operator<(const Molecule&,const Molecule&);
bool operator>(const Molecule&,const Molecule&);
bool operator<=(const Molecule&,const Molecule&);
bool operator>=(const Molecule&,const Molecule&);
float operator-(const Molecule&,const Molecule&);//difference between Y only
float Dist(const Molecule&,const Molecule&);
int __fastcall MolCompare(void*,void*);
ostream& operator<<(ostream&,Molecule&);
istream& operator>>(istream&,Molecule&);
TStrings* operator<<(TStrings*,MolecList&);
istream& operator>>(istream&,MolecList&);
//TStrings* operator<<(TStrings*,MolecIntList&);
//-----------------------------------------------------------------
#endif