package com.azoroapps.calcVault.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.azoroapps.calcVault.R;
import com.azoroapps.calcVault.adapter.ServerListRVAdapter;
import com.azoroapps.calcVault.interfaces.ChangeServer;
import com.azoroapps.calcVault.interfaces.NavItemClickListener;
import com.azoroapps.calcVault.model.Server;

import java.util.ArrayList;
import java.util.Objects;

import com.azoroapps.calcVault.utilities.Utils;


public class VpnActivity extends AppCompatActivity implements NavItemClickListener {
    private final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList serverLists;
    private DrawerLayout drawer;
    private ChangeServer changeServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);

        // Initialize all variable
        initializeAll();

        ImageButton menuRight = findViewById(R.id.navbar_right);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Server List recycler view initialize
        if (serverLists != null) {
            ServerListRVAdapter serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }

    }

    /**
     * Initialize all object, listener etc
     */
    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);

        fragment = new VpnFragment();
        serverListRv = findViewById(R.id.recyclerview_serverList);
        serverListRv.setHasFixedSize(true);

        serverListRv.setLayoutManager(new LinearLayoutManager(this));

        serverLists = getServerList();
        changeServer = (ChangeServer) fragment;

    }

    /**
     * Close navigation drawer
     */
    public void closeDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    /**
     * Generate server array list
     */
    private ArrayList getServerList() {

        ArrayList<Server> servers = new ArrayList<>();

        servers.add(new Server("Korea",
                Utils.getImgURL(R.drawable.korea),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("Japan",
                Utils.getImgURL(R.drawable.japan),
                "japan.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("USA",
                Utils.getImgURL(R.drawable.usa_flag),
                "usa.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("France",
                Utils.getImgURL(R.drawable.france_flag),
                "france.ovpn",
                "vpnbook",
                "3x58UVx"
        ));


        return servers;
    }

    /**
     * On navigation item click, close drawer and change server
     * @param index: server index
     */
    @Override
    public void clickedItem(int index) {
        closeDrawer();
        changeServer.newServer((Server) serverLists.get(index));
    }
}
