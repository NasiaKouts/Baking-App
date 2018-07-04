package kouts.nasia.aueb.gr.bakingrecipes.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kouts.nasia.aueb.gr.bakingrecipes.Models.Step;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.StepsHolder>{

    private final Context context;
    private int selected = -1;

    public void setSelectedItemIndex(int index) {
        selected = index;
        notifyDataSetChanged();
    }

    public interface StepSelectedAdapterListener {
        void stepSelected(int selectedStepPos);
    }

    private final static List<Integer> boxColors = Arrays.asList(
                                                    0xffe57373,
                                                        0xfff06292,
                                                        0xffba68c8,
                                                        0xff9575cd,
                                                        0xff7986cb,
                                                        0xff64b5f6,
                                                        0xff4dd0e1,
                                                        0xff4db6ac,
                                                        0xffaed581,
                                                        0xffffd54f,
                                                        0xffffb74d,
                                                        0xffff8a65
                                                        );

    private final static List<Integer> boxcolorsOpacity = Arrays.asList(
            0xcce57373,
            0xaaf06292,
            0xaaba68c8,
            0xaa9575cd,
            0xaa7986cb,
            0xaa64b5f6,
            0xaa4dd0e1,
            0xaa4db6ac,
            0xaaaed581,
            0xaaffd54f,
            0xaaffb74d,
            0xaaff8a65
    );

    private ArrayList<Step> stepsList;
    private StepSelectedAdapterListener stepSelectedCallback;
    private RecyclerView recyclerView;

    public class StepsHolder extends RecyclerView.ViewHolder{
        View boxColorView;
        TextView stepTv;
        LinearLayout rootLy;

        public StepsHolder(View view){
            super(view);
            stepTv = view.findViewById(R.id.text_view_step_item);
            boxColorView = view.findViewById(R.id.color_box_step_item);
            rootLy = view.findViewById(R.id.step_item_root);
        }
    }

    public StepsRecyclerViewAdapter(Context context, ArrayList<Step> stepsList, StepSelectedAdapterListener stepSelectedCallback, RecyclerView recyclerView){
        this.context = context;
        this.stepSelectedCallback = stepSelectedCallback;
        this.recyclerView = recyclerView;
        if(stepsList == null) this.stepsList = new ArrayList<>();
        else this.stepsList = stepsList;
    }

    @NonNull
    @Override
    public StepsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_item, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = recyclerView.getChildLayoutPosition(view);
                stepSelectedCallback.stepSelected(selected);
                // in order to set color to the selected one
                if(context.getResources().getBoolean(R.bool.isTablet)) {
                    notifyDataSetChanged();
                }
            }
        });

        return new StepsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsHolder holder, int position) {
        Step step = stepsList.get(position);
        int colorIndex = position % boxColors.size();
        holder.boxColorView.setBackgroundColor(boxColors.get(colorIndex));
        holder.stepTv.setText(step.getId() + ".\t" + step.getShortDescription());
        if(context.getResources().getBoolean(R.bool.isTablet) &&
                selected == position) {
            holder.rootLy.setBackgroundColor(boxcolorsOpacity.get(colorIndex));
        }
        else if(context.getResources().getBoolean(R.bool.isTablet)){
            holder.rootLy.setBackgroundColor(context.getResources().getColor(R.color.colorIcons));
        }
    }

    @Override
    public int getItemCount() {
        return stepsList == null ? 0 : stepsList.size();
    }

}
