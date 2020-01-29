package com.verbole.dcad.tabula;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.verbole.dcad.tabula.RoomDatabase.Baseentrees;

import java.util.List;

// test avec Room DB
public class FragmentDictionnaire2 extends Fragment {

    float largeurFenetre = 0;
    int taillePolice = 14;
    long dureeAnimations = 600;

    private boolean mTwoPane;
    private boolean isDefVisible = false;
    private boolean aStoppe = false;
    String texteHtmlCourant = "";

    private FragmentDictViewModel2 mViewModel;

    public FragmentDictionnaire2() {
        super();
    }
/* dispo ds androidx.appcompat:appcompat:1.1.0 ??
    public FragmentDictionnaire2(int contentLayoutId) {
        super(contentLayoutId);
    }
*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_dictionnaire_rv, container, false);
        mViewModel =  ViewModelProviders.of(getActivity()).get(FragmentDictViewModel2.class);

        mTwoPane = StyleHtml.verifieEcranEstTablette(this.getActivity());
        if (!mTwoPane) {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
        else {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        ListView listViewMots = view.findViewById(R.id.TableListeMots);

        mViewModel.getEntrees().observe(getViewLifecycleOwner(), listEntrees -> {
            DictAdapter adapter = new DictAdapter(getContext(),android.R.layout.simple_list_item_1,listEntrees);
            Log.d("FragDict2","liste : " + listEntrees.size() + " ??");
            listViewMots.setAdapter(adapter);

        });



/*
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMots);
        final RoomRecyclerAdapter adapter = new RoomRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel.getEntrees().observe(getViewLifecycleOwner(), new Observer<List<Baseentrees>>() {
            @Override
            public void onChanged(List<Baseentrees> baseentrees) {
                adapter.setEntrees(baseentrees);
            }
        });
*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class DictAdapter extends ArrayAdapter<Baseentrees> {

        public DictAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }
        public DictAdapter(@NonNull Context context, int resource, @NonNull List<Baseentrees> objects) {
            super(context, resource, objects);
        }

        @Override
        public @NonNull
        View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //   v = inflater.inflate(android.R.layout.simple_list_item_1, null);
                v = inflater.inflate(R.layout.list_row_texte, null);
            }

            //  TextView textVue = (TextView) v.findViewById(android.R.id.text1);
            TextView textVue = (TextView) v.findViewById(R.id.listRowVue);
            //textVue.setText(cursor.getString(getCursor().getColumnIndex("word")));
            Baseentrees obj = getItem(position);
            String mot = obj.getEntree();

            textVue.setText(mot);

            return v;
        }


    }
}
