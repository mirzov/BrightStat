#include <Math.hpp>
#include "Molecules.h"
//-----------------------------------------------------------------
Molecule::Molecule()
{
   x=y=0;
   I=0;
}
//-----------------------------------------------------------------
Molecule::Molecule(int x0,int y0,float I0)
{
   x=x0; y=y0;
   I=I0;
}
//-----------------------------------------------------------------
Molecule::Molecule(const Molecule &m):x(m.x),y(m.y),I(m.I)
{
  // x=m.x; y=m.y; I=m.I;
}
//-----------------------------------------------------------------
Molecule& Molecule::operator=(const Molecule &m)
{
   x=m.x; y=m.y; I=m.I;
   return *this;
}
//-----------------------------------------------------------------
bool Molecule::operator==(const Molecule &m)
{
   return x==m.x && y==m.y;
}
//-----------------------------------------------------------------
bool Molecule::operator!=(const Molecule &m)
{
   return x!=m.x || y!=m.y;
}
//-----------------------------------------------------------------
bool operator<(const Molecule &m1, const Molecule &m2)
{
   if(m1.y<m2.y) return true;
   if(m1.y>m2.y) return false;
   return m1.x<m2.x;
}
//-----------------------------------------------------------------
bool operator<=(const Molecule &m1, const Molecule &m2)
{
   if(m1.y<m2.y) return true;
   if(m1.y>m2.y) return false;
   return m1.x<=m2.x;
}
//-----------------------------------------------------------------
bool operator>(const Molecule &m1, const Molecule &m2)
{
   if(m1.y>m2.y) return true;
   if(m1.y<m2.y) return false;
   return m1.x>m2.x;
}
//-----------------------------------------------------------------
bool operator>=(const Molecule &m1, const Molecule &m2)
{
   if(m1.y>m2.y) return true;
   if(m1.y<m2.y) return false;
   return m1.x>=m2.x;
}
//-----------------------------------------------------------------
float Dist(const Molecule &m1, const Molecule &m2)
{
   return Hypot(m1.x-m2.x,m1.y-m2.y);
}
//-----------------------------------------------------------------
float operator-(const Molecule &m1,const Molecule &m2)
{
   return m1.y-m2.y;
}
//-----------------------------------------------------------------
int __fastcall MolCompare(void *p1,void *p2)
{
   Molecule *m1=(Molecule*)p1,*m2=(Molecule*)p2;
   if(m1->y<m2->y) return -1;
   if(m1->y>m2->y) return 1;
   if(m1->x>m2->x) return 1;
   if(m1->x<m2->x) return -1;
   return 0;
}
//-----------------------------------------------------------------
ostream& operator<<(ostream &o,Molecule &m)
{
   o<<m.x<<"   "<<m.y<<"   "<<m.I;
   return o;
}
//-----------------------------------------------------------------
istream& operator>>(istream &i,Molecule &m)
{
   i>>m.x>>m.y>>m.I;
   return i;
}
//-----------------------------------------------------------------
AnsiString& operator<<(AnsiString &s,Molecule &m)
{
   s+=(AnsiString)m.x+"   "+(AnsiString)m.y+"   "+(AnsiString)m.I+"   ";
   return s;
}
//-----------------------------------------------------------------
__fastcall MolecList::MolecList(int StartCapac)
{
   Capacity=StartCapac;
}
//-----------------------------------------------------------------
__fastcall MolecList::MolecList(MolecList *ListToCopy)
{
   Capacity=ListToCopy->Capacity;
   Molecule* moltmp;//=NULL;
   for(int i=0;i<ListToCopy->Count;i++)
   {
      if(ListToCopy->Items[i])
      {
         moltmp=new Molecule(*((Molecule*)ListToCopy->Items[i]));
         TList::Add(moltmp);
      }else Count++;
   }
}
//-----------------------------------------------------------------
__fastcall MolecList::~MolecList()
{
   Pack();
   Capacity=Count;
   for(int i=Count-1;i>=0;i--)
   {
      delete (Molecule*)Items[i];
      TList::Delete(i);
   }
}
//-----------------------------------------------------------------
void __fastcall MolecList::Clear()
{
   Pack();
   Capacity=1.5*Count;
   for(int i=Count-1;i>=0;i--)
   {
      delete (Molecule*)Items[i];
      TList::Delete(i);
   }
//   Capacity=0;
}
//-----------------------------------------------------------------
int __fastcall MolecList::Add(Molecule& m)
{
   Molecule *mnew=new Molecule(m);
   return TList::Add(mnew);
}
//-----------------------------------------------------------------
void __fastcall MolecList::Delete(int i)
{
   delete (Molecule*)Items[i];
   TList::Delete(i);
}
//-----------------------------------------------------------------
Molecule& MolecList::Mol(int i)
{
   return *(Molecule*)Items[i];
}
//-----------------------------------------------------------------
/*MolecList& MolecList::operator=(MolecList* ListToCopy)
{
   Pack();
   for(int i=0;i<Count;i++) delete Items[i];
   Clear();
   Capacity=ListToCopy->Capacity;
   for(int i=0;i<Capacity;i++)
   {
      if(ListToCopy->Items[i])
      {
         Items[i]=new Molecule(*((Molecule*)ListToCopy->Items[i]));
      }
   }
   return *this;
} */
//-----------------------------------------------------------------
MolecListArray::MolecListArray(void)
{
   Lists=new MolecList*;
   Lists[0]=NULL;
   N=1;
}
//-----------------------------------------------------------------
MolecListArray::MolecListArray(int NLists)
{
   N=NLists;
   Lists=new MolecList*[N];
   for(int i=0;i<N;i++) Lists[i]=NULL;
}
//-----------------------------------------------------------------
MolecListArray::MolecListArray(const MolecListArray& ma)
{
   N=ma.N;
   Lists=new MolecList*[N];
   for(int i=0;i<N;i++)
   {
      if(ma.Lists[i]) Lists[i]=new MolecList(ma.Lists[i]);
      else Lists[i]=NULL;
   }
}
//-----------------------------------------------------------------
MolecListArray::~MolecListArray(void)
{
   for(int i=0;i<N;i++) if(Lists[i]) delete Lists[i];
   delete[] Lists;
}
//-----------------------------------------------------------------
MolecListArray& MolecListArray::operator=(const MolecListArray& ma)
{
   for(int i=0;i<N;i++) if(Lists[i]) delete Lists[i];
   delete[] Lists;
   N=ma.N;
   Lists=new MolecList*[N];
   for(int i=0;i<N;i++)
   {
      if(ma.Lists[i]) Lists[i]=new MolecList(ma.Lists[i]);
      else Lists[i]=NULL;
   }
   return *this;
}
//-----------------------------------------------------------------
int MolecListArray::AddList(MolecList* NewList,int pos)
{
   if (pos>=N || pos<0) return 1;
   if(Lists[pos]) delete Lists[pos];
   Lists[pos]=NewList;
   return 0;
}
//-----------------------------------------------------------------
void MolecListArray::SetNumberOfLists(int NewN)
{
   for(int i=0;i<N;i++) delete Lists[i];
   delete[] Lists;
   Lists=new MolecList*[NewN];
   N=NewN;
}
//-----------------------------------------------------------------
void MolecListArray::DeleteMolecule(int nmol)
{
   if(nmol<0) return;
   for(int i=0;i<N;i++)
   {
      if(Lists[i]->Capacity>nmol) Lists[i]->Delete(nmol);
   }
}
//-----------------------------------------------------------------
MolecList* MolecListArray::operator[](int i)
{
   if(i<0) i=0;
   if(i>=N) i=N-1;
   return Lists[i];
}
//-----------------------------------------------------------------
istream& operator>>(istream &is,MolecList &ml)
{
   Molecule m,m0;
   int tempCapas=ml.Capacity;
   ml.Clear();
   ml.Capacity=tempCapas;
   is>>m;
   while(m!=m0)
   {
      ml.Add(m);
      m=m0;
      is>>m;
   }
   return is;
}
//-----------------------------------------------------------------
TStrings* operator<<(TStrings *strs,MolecList &ml)
{
   AnsiString str;
   for(int i=0;i<ml.Count;i++)
   {
      str="";
      str<<ml.Mol(i);
      strs->Add(str);
   }
   return strs;
}
//-----------------------------------------------------------------
__fastcall MolecIntList::~MolecIntList()
{
   Pack();
   for(int i=0;i<Count;i++) delete Items[i];
}
//-----------------------------------------------------------------
TStrings* operator<<(TStrings* strs,MolecIntList& mil)
{
   for(int i=0;i<mil.Count;i++)
   {
      if(strs->Count<=i) strs->Add("");
      AnsiString &str=strs->Strings[i];
      vector<float> &vp=*((vector<float>*)mil.Items[i]);
      for(unsigned int j=0;j<vp.size();j++) str+=(AnsiString)vp[j]+"   ";
      strs->Strings[i]=str;
   }
   return strs;
}
//-----------------------------------------------------------------

