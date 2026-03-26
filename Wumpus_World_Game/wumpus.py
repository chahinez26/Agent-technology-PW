import pygame
import sys
from enum import Enum, IntEnum
import random
from collections import deque
import itertools
import os

# ─────────────────────────────────────────────
# Configuration
# ─────────────────────────────────────────────
CELL_SIZE = 100
GRID_SIZE = 4
WINDOW_SIZE = CELL_SIZE * GRID_SIZE
INFO_PANEL_WIDTH = 350
WINDOW_WIDTH = WINDOW_SIZE + INFO_PANEL_WIDTH
WINDOW_HEIGHT = WINDOW_SIZE + 100

# Couleurs
WHITE      = (255, 255, 255)
BLACK      = (0,   0,   0)
GRAY       = (200, 200, 200)
DARK_GRAY  = (100, 100, 100)
GREEN      = (0,   200, 0)
RED        = (255, 0,   0)
YELLOW     = (255, 215, 0)
BLUE       = (100, 150, 255)
DARK_GREEN = (0,   120, 0)
ORANGE     = (255, 165, 0)
BROWN      = (101, 67,  33)

# ─────────────────────────────────────────────
# Sprites dessinés programmatiquement
# (utilisés si aucune image PNG n'est trouvée)
# ─────────────────────────────────────────────

def create_wumpus_sprite(size=70):
    surf = pygame.Surface((size, size), pygame.SRCALPHA)
    cx, cy = size // 2, size // 2
    r = size // 2 - 4
    pygame.draw.ellipse(surf, (180, 20, 20), (cx - r, cy - r + 6, r * 2, r * 2 - 4))
    horn = (120, 10, 10)
    pygame.draw.polygon(surf, horn, [(cx-14, cy-r+6),(cx-24, cy-r-12),(cx-5, cy-r+2)])
    pygame.draw.polygon(surf, horn, [(cx+14, cy-r+6),(cx+24, cy-r-12),(cx+5, cy-r+2)])
    for ex in [cx - 12, cx + 12]:
        pygame.draw.circle(surf, YELLOW, (ex, cy - 8), 8)
        pygame.draw.circle(surf, BLACK,  (ex, cy - 8), 3)
    mouth_y = cy + 10
    pygame.draw.rect(surf, (60, 0, 0), (cx-16, mouth_y, 32, 12), border_radius=4)
    for i in range(4):
        pygame.draw.rect(surf, WHITE, (cx-14+i*9, mouth_y, 7, 10), border_radius=2)
    pygame.draw.ellipse(surf, (80, 0, 0), (cx-r, cy-r+6, r*2, r*2-4), 2)
    return surf

def create_gold_sprite(size=60):
    surf = pygame.Surface((size, size), pygame.SRCALPHA)
    cx, cy = size // 2, size // 2
    body = pygame.Rect(cx-20, cy-12, 40, 26)
    pygame.draw.rect(surf, (218, 165, 32), body, border_radius=6)
    pygame.draw.rect(surf, (255, 220, 50), body.inflate(-6, -6), border_radius=5)
    pygame.draw.rect(surf, (255, 255, 180), (body.x+5, body.y+4, 14, 6), border_radius=3)
    font = pygame.font.SysFont("Arial", 16, bold=True)
    lbl = font.render("Au", True, (120, 80, 0))
    surf.blit(lbl, (cx - lbl.get_width()//2, cy - lbl.get_height()//2 + 2))
    pygame.draw.rect(surf, (160, 120, 0), body, 2, border_radius=6)
    return surf

def create_pit_sprite(size=80):
    surf = pygame.Surface((size, size), pygame.SRCALPHA)
    cx, cy = size // 2, size // 2
    r = size // 2 - 4
    pygame.draw.circle(surf, (70, 70, 70), (cx, cy), r)
    pygame.draw.ellipse(surf, (20, 20, 20), (cx-r+8, cy-r+10, (r-8)*2, (r-10)*2))
    for i in range(5):
        pygame.draw.circle(surf, (10, 10, 10), (cx, cy+i*2), max(2, r-8-i*4))
    pygame.draw.circle(surf, (110, 110, 110), (cx, cy), r, 3)
    pygame.draw.circle(surf, (40,  40,  40),  (cx, cy), r-8, 2)
    return surf

def create_arrow_sprite(size=50):
    surf = pygame.Surface((size, size), pygame.SRCALPHA)
    cx, cy = size // 2, size // 2
    pygame.draw.rect(surf, BROWN, (4, cy-3, size-18, 6), border_radius=2)
    pygame.draw.polygon(surf, (200, 120, 40), [(size-3, cy),(size-16, cy-10),(size-16, cy+10)])
    pygame.draw.polygon(surf, (180, 180, 180), [(4, cy),(14, cy-8),(14, cy+8)])
    return surf

def create_agent_sprite(size=70):
    surf = pygame.Surface((size, size), pygame.SRCALPHA)
    cx, cy = size // 2, size // 2
    s = size // 2 - 6
    # Triangle pointant vers la droite (direction par défaut)
    pts = [(cx+s, cy), (cx-s, cy-s), (cx-s, cy+s)]
    pygame.draw.polygon(surf, (30, 180, 255), pts)
    pygame.draw.polygon(surf, WHITE, pts, 2)
    pygame.draw.circle(surf, WHITE, (cx+s//2, cy), 5)
    pygame.draw.circle(surf, BLACK, (cx+s//2, cy), 2)
    return surf

# ─────────────────────────────────────────────
# Chargeur d'images avec fallback sprite
# ─────────────────────────────────────────────

def load_image(filename, fallback_fn, size):
    """
    Cherche `filename` dans le même dossier que le script.
    Si trouvé → redimensionne à (size, size).
    Sinon    → utilise la fonction de dessin fallback_fn(size).
    """
    path = os.path.join(os.path.dirname(os.path.abspath(__file__)), filename)
    if os.path.exists(path):
        try:
            img = pygame.image.load(path).convert_alpha()
            return pygame.transform.smoothscale(img, (size, size))
        except Exception:
            pass
    return fallback_fn(size)


# ─────────────────────────────────────────────
# Enums & helpers
# ─────────────────────────────────────────────

class CellType(Enum):
    EMPTY  = 0
    PIT    = 1
    WUMPUS = 2
    GOLD   = 3

class Direction(IntEnum):
    RIGHT = 0
    DOWN  = 1
    LEFT  = 2
    UP    = 3

class Action(Enum):
    FORWARD    = "MOVE_FORWARD"
    TURN_LEFT  = "TURN_LEFT"
    TURN_RIGHT = "TURN_RIGHT"
    GRAB       = "GRAB"
    SHOOT      = "SHOOT"
    CLIMB      = "CLIMB"

def get_adjacent(x, y):
    adj = []
    for dx, dy in [(0,1),(0,-1),(1,0),(-1,0)]:
        nx, ny = x+dx, y+dy
        if 0 <= nx < GRID_SIZE and 0 <= ny < GRID_SIZE:
            adj.append((nx, ny))
    return adj


# ─────────────────────────────────────────────
# Agent — avec corrections logiques
# ─────────────────────────────────────────────

class Agent:
    def __init__(self):
        self.x = 0
        self.y = 0
        self.direction  = Direction.RIGHT
        self.has_arrow  = True
        self.has_gold   = False
        self.is_alive   = True
        self.climbed_out = False

        self.visited    = set()
        self.safe_cells = {(0, 0)}
        self.perceptions = {}           # (x,y) → {breeze, stench, glitter, scream}

        # Ensemble des cellules où le Wumpus PEUT être (du point de vue de l'agent)
        self.valid_wumpus = {(x,y) for x in range(GRID_SIZE)
                                    for y in range(GRID_SIZE) if (x,y) != (0,0)}
        self.wumpus_dead = False

        self.action_plan = deque()
        self.score  = 0
        self.status = "Initializing..."

    def current_pos(self):
        return (self.x, self.y)

    def update_kb(self, x, y, percepts):
        self.visited.add((x, y))
        self.perceptions[(x, y)] = percepts
        self.safe_cells.add((x, y))

        # ── Wumpus filtering ──────────────────────────────────────────────
        # BUG FIX : on ne peut pas simplement faire intersection_update avec
        # les adjacents du dernier stench — il faut recalculer à partir de
        # TOUTES les observations stockées.
        if self.wumpus_dead:
            self.valid_wumpus = set()
        else:
            # Retirer la case visitée elle-même (l'agent est vivant → pas de Wumpus ici)
            self.valid_wumpus.discard((x, y))

            # Reconstruction depuis zéro à partir de toutes les cases visitées
            candidate = {(cx, cy) for cx in range(GRID_SIZE)
                                   for cy in range(GRID_SIZE)
                                   if (cx, cy) not in self.visited}

            for vx, vy in self.visited:
                p = self.perceptions[(vx, vy)]
                adj = set(get_adjacent(vx, vy))
                if p['stench']:
                    # Le Wumpus est forcément dans un adjacent
                    candidate &= adj
                else:
                    # Le Wumpus n'est PAS dans les adjacents
                    candidate -= adj

            # Si l'ensemble devient vide par contradiction, on garde l'ancien
            if candidate:
                self.valid_wumpus = candidate

        self.deduce_safe_cells()

    def deduce_safe_cells(self):
        """
        Model-checking pour les puits.
        BUG FIX : une cellule visitée peut être la source d'un breeze même si
        le pit correspondant n'est PAS dans la frontière courante — on doit
        aussi tenir compte des pits déjà confirmés hors frontière.
        """
        all_cells = {(r,c) for r in range(GRID_SIZE) for c in range(GRID_SIZE)}
        unvisited  = all_cells - self.visited

        # Frontière = cases non visitées adjacentes à au moins une case visitée
        frontier = set()
        for x, y in self.visited:
            for nx, ny in get_adjacent(x, y):
                if (nx, ny) in unvisited:
                    frontier.add((nx, ny))
        frontier = list(frontier)
        n = len(frontier)

        valid_models = []
        for i in range(1 << n):
            model = {frontier[j]: bool((i >> j) & 1) for j in range(n)}

            # Contrainte : max 3 pits au total
            if sum(model.values()) > 3:
                continue

            consistent = True
            for vx, vy in self.visited:
                breeze = self.perceptions[(vx, vy)]['breeze']
                adj    = get_adjacent(vx, vy)
                # Pits parmi les adjacents qui sont DANS la frontière
                adj_frontier_pits = [model[c] for c in adj if c in model]
                # Adjacents non visités et hors frontière → inconnus, on les ignore
                # (on ne peut rien conclure sur eux)

                if breeze:
                    # Il FAUT au moins un pit adjacent ; s'ils sont tous à False
                    # dans la frontière ET qu'il n'y a pas d'adjacent inconnu hors
                    # frontière, c'est incohérent.
                    adj_unknown_outside = [c for c in adj
                                           if c in unvisited and c not in model]
                    if not any(adj_frontier_pits) and not adj_unknown_outside:
                        consistent = False
                        break
                else:
                    # Aucun pit adjacent → tous les adjacents dans la frontière sont False
                    if any(adj_frontier_pits):
                        consistent = False
                        break

            if consistent:
                valid_models.append(model)

        if valid_models:
            for cell in frontier:
                safe_from_pits   = all(not m[cell] for m in valid_models)
                safe_from_wumpus = cell not in self.valid_wumpus
                if safe_from_pits and safe_from_wumpus:
                    self.safe_cells.add(cell)

    def plan_path_to(self, target):
        """BFS uniquement sur les cellules sûres."""
        queue   = deque([(self.current_pos(), [])])
        visited = {self.current_pos()}
        while queue:
            curr, path = queue.popleft()
            if curr == target:
                return path
            for nx, ny in get_adjacent(*curr):
                if (nx, ny) not in visited and (nx, ny) in self.safe_cells:
                    visited.add((nx, ny))
                    queue.append(((nx, ny), path + [(nx, ny)]))
        return None

    def convert_path_to_actions(self, path):
        """Convertit un chemin en liste d'actions FORWARD / TURN_*."""
        actions  = []
        curr_dir = self.direction
        curr_pos = self.current_pos()
        for nxt in path:
            dx = nxt[0] - curr_pos[0]
            dy = nxt[1] - curr_pos[1]
            if   dx ==  1: wanted = Direction.RIGHT
            elif dx == -1: wanted = Direction.LEFT
            elif dy ==  1: wanted = Direction.DOWN
            else:           wanted = Direction.UP
            while curr_dir != wanted:
                diff = (wanted.value - curr_dir.value) % 4
                if diff == 1:
                    actions.append(Action.TURN_RIGHT)
                    curr_dir = Direction((curr_dir.value + 1) % 4)
                elif diff == 3:
                    actions.append(Action.TURN_LEFT)
                    curr_dir = Direction((curr_dir.value - 1) % 4)
                else:   # 180° → deux droites
                    actions.append(Action.TURN_RIGHT)
                    curr_dir = Direction((curr_dir.value + 1) % 4)
            actions.append(Action.FORWARD)
            curr_pos = nxt
        return actions

    def _sim_dir_after(self, actions, start_dir):
        """Simule la direction après une séquence d'actions (turns seulement)."""
        d = start_dir
        for a in actions:
            if a == Action.TURN_RIGHT: d = Direction((d.value + 1) % 4)
            elif a == Action.TURN_LEFT:  d = Direction((d.value - 1) % 4)
        return d

    def decide_action(self):
        if not self.is_alive or self.climbed_out:
            return None

        # Si un plan est en cours, l'exécuter
        if self.action_plan:
            return self.action_plan.popleft()

        percepts = self.perceptions.get(self.current_pos(), {})

        # 1) Ramasser l'or s'il brille
        if percepts.get('glitter') and not self.has_gold:
            self.status = "Glitter détecté ! Je prends l'or."
            return Action.GRAB

        # 2) Rentrer avec l'or
        if self.has_gold:
            if self.current_pos() == (0, 0):
                self.status = "À la maison avec l'or ! Je sors."
                return Action.CLIMB
            self.status = "Retour vers (0,0) avec l'or..."
            path = self.plan_path_to((0, 0))
            if path:
                self.action_plan.extend(self.convert_path_to_actions(path))
                return self.action_plan.popleft()
            self.status = "Piégé ! Impossible de revenir."
            return None

        # 3) Explorer les cases sûres non visitées
        unvisited_safe = self.safe_cells - self.visited
        if unvisited_safe:
            paths = [(p, self.plan_path_to(p)) for p in unvisited_safe]
            paths = [(t, p) for t, p in paths if p is not None]
            if paths:
                target, shortest = min(paths, key=lambda x: len(x[1]))
                self.status = f"Exploration → {target}"
                self.action_plan.extend(self.convert_path_to_actions(shortest))
                return self.action_plan.popleft()

        # 4) Tirer sur le Wumpus si sa position est certaine
        if len(self.valid_wumpus) == 1 and self.has_arrow and not self.wumpus_dead:
            wloc = list(self.valid_wumpus)[0]
            # Trouver la meilleure case de tir (même ligne ou colonne, déjà visitée)
            candidates = [(sx, sy) for sx, sy in self.visited
                          if sx == wloc[0] or sy == wloc[1]]
            for sx, sy in candidates:
                path_to_shoot = self.plan_path_to((sx, sy))
                if path_to_shoot is None:
                    continue
                self.status = f"Chasse le Wumpus en {wloc} !"
                move_acts = self.convert_path_to_actions(path_to_shoot)

                # Direction souhaitée pour tirer
                dx2 = wloc[0] - sx; dy2 = wloc[1] - sy
                if   dx2 > 0: shoot_dir = Direction.RIGHT
                elif dx2 < 0: shoot_dir = Direction.LEFT
                elif dy2 > 0: shoot_dir = Direction.DOWN
                else:          shoot_dir = Direction.UP

                # BUG FIX : simuler la direction réelle après move_acts
                final_dir = self._sim_dir_after(move_acts, self.direction)
                turn_acts = []
                while final_dir != shoot_dir:
                    diff = (shoot_dir.value - final_dir.value) % 4
                    if diff == 1:
                        turn_acts.append(Action.TURN_RIGHT)
                        final_dir = Direction((final_dir.value + 1) % 4)
                    elif diff == 3:
                        turn_acts.append(Action.TURN_LEFT)
                        final_dir = Direction((final_dir.value - 1) % 4)
                    else:
                        turn_acts.append(Action.TURN_RIGHT)
                        final_dir = Direction((final_dir.value + 1) % 4)

                self.action_plan.extend(move_acts + turn_acts + [Action.SHOOT])
                return self.action_plan.popleft()

        # 5) Plus rien de sûr → battre en retraite
        # BUG FIX : si déjà en (0,0), grimper directement sans attendre un chemin
        self.status = "Retraite vers (0,0)..."
        if self.current_pos() == (0, 0):
            return Action.CLIMB
        path = self.plan_path_to((0, 0))
        if path:
            self.action_plan.extend(self.convert_path_to_actions(path))
            return self.action_plan.popleft()

        self.status = "Piégé, nulle part où aller..."
        return None


# ─────────────────────────────────────────────
# World
# ─────────────────────────────────────────────

class World:
    def __init__(self):
        self.grid = [[CellType.EMPTY]*GRID_SIZE for _ in range(GRID_SIZE)]
        self.wumpus_alive = True
        self.game_over    = False
        self.message      = "Agent initialisé."
        self.agent        = Agent()
        self.wumpus_pos   = None
        self.gold_pos     = None
        self.pits         = []
        self._place_elements()
        self.agent.update_kb(0, 0, self.get_perceptions(0, 0))
        self.agent.status = "Monde généré. Prêt."

    def _place_elements(self):
        positions = [(x,y) for x in range(GRID_SIZE)
                           for y in range(GRID_SIZE) if (x,y) != (0,0)]
        random.shuffle(positions)
        self.wumpus_pos = positions.pop()
        self.grid[self.wumpus_pos[1]][self.wumpus_pos[0]] = CellType.WUMPUS
        self.gold_pos   = positions.pop()
        self.grid[self.gold_pos[1]][self.gold_pos[0]] = CellType.GOLD
        self.pits = []
        for _ in range(3):
            pos = positions.pop()
            self.pits.append(pos)
            self.grid[pos[1]][pos[0]] = CellType.PIT

    def get_perceptions(self, x, y):
        p = {'breeze': False, 'stench': False, 'glitter': False, 'scream': False}
        for nx, ny in get_adjacent(x, y):
            if self.grid[ny][nx] == CellType.PIT:
                p['breeze'] = True
            if self.grid[ny][nx] == CellType.WUMPUS and self.wumpus_alive:
                p['stench'] = True
        if self.grid[y][x] == CellType.GOLD and not self.agent.has_gold:
            p['glitter'] = True
        return p

    def execute_action(self, action):
        if self.game_over or action is None:
            return
        self.agent.score -= 1

        if action == Action.TURN_LEFT:
            self.agent.direction = Direction((self.agent.direction.value - 1) % 4)
            self.message = "Tourne à gauche."
        elif action == Action.TURN_RIGHT:
            self.agent.direction = Direction((self.agent.direction.value + 1) % 4)
            self.message = "Tourne à droite."

        elif action == Action.FORWARD:
            dx, dy = 0, 0
            if   self.agent.direction == Direction.RIGHT: dx =  1
            elif self.agent.direction == Direction.DOWN:  dy =  1
            elif self.agent.direction == Direction.LEFT:  dx = -1
            elif self.agent.direction == Direction.UP:    dy = -1
            nx, ny = self.agent.x + dx, self.agent.y + dy
            if 0 <= nx < GRID_SIZE and 0 <= ny < GRID_SIZE:
                self.agent.x, self.agent.y = nx, ny
                self.message = "Avance."
                cell = self.grid[ny][nx]
                if cell == CellType.PIT:
                    self.agent.is_alive = False
                    self.game_over = True
                    self.message = "GAME OVER — tombé dans un puits !"
                    self.agent.score -= 1000
                    return
                if cell == CellType.WUMPUS and self.wumpus_alive:
                    self.agent.is_alive = False
                    self.game_over = True
                    self.message = "GAME OVER — dévoré par le Wumpus !"
                    self.agent.score -= 1000
                    return
                percepts = self.get_perceptions(nx, ny)
                self.agent.update_kb(nx, ny, percepts)
            else:
                self.message = "Mur !"

        elif action == Action.GRAB:
            if (self.agent.x, self.agent.y) == self.gold_pos and not self.agent.has_gold:
                self.agent.has_gold = True
                self.agent.score   += 1000
                self.message = "L'or est à moi !"
            else:
                self.message = "Rien à ramasser."

        elif action == Action.SHOOT:
            if self.agent.has_arrow:
                self.agent.has_arrow = False
                self.agent.score    -= 10
                dx, dy = 0, 0
                if   self.agent.direction == Direction.RIGHT: dx =  1
                elif self.agent.direction == Direction.DOWN:  dy =  1
                elif self.agent.direction == Direction.LEFT:  dx = -1
                elif self.agent.direction == Direction.UP:    dy = -1
                ax, ay = self.agent.x, self.agent.y
                killed = False
                while 0 <= ax + dx < GRID_SIZE and 0 <= ay + dy < GRID_SIZE:
                    ax += dx; ay += dy
                    if (ax, ay) == self.wumpus_pos and self.wumpus_alive:
                        self.wumpus_alive        = False
                        self.agent.wumpus_dead   = True
                        self.agent.valid_wumpus  = set()
                        self.agent.score        += 500
                        killed = True
                        break
                if killed:
                    self.message = "AAAARGH ! Wumpus tué !"
                    for vx, vy in list(self.agent.visited):
                        self.agent.perceptions[(vx,vy)]['stench'] = False
                    self.agent.deduce_safe_cells()
                else:
                    self.message = "Raté !"
            else:
                self.message = "Plus de flèche."

        elif action == Action.CLIMB:
            if self.agent.x == 0 and self.agent.y == 0:
                self.game_over        = True
                self.agent.climbed_out = True
                if self.agent.has_gold:
                    self.agent.score += 100
                    self.message = "VICTOIRE ! Sorti avec l'or !"
                else:
                    self.message = "Sorti sans l'or."
            else:
                self.message = "Sortie uniquement en (0,0)."


# ─────────────────────────────────────────────
# Game / Rendu
# ─────────────────────────────────────────────

class Game:
    def __init__(self):
        pygame.init()
        self.screen = pygame.display.set_mode((WINDOW_WIDTH, WINDOW_HEIGHT))
        pygame.display.set_caption("Wumpus World — Agent IA Logique")
        self.clock      = pygame.time.Clock()
        self.font       = pygame.font.Font(None, 24)
        self.small_font = pygame.font.Font(None, 20)
        self.world      = World()
        self.auto_play  = True

        self._load_sprites()

    def _load_sprites(self):
        S = CELL_SIZE - 20
        self.img_wumpus = load_image("wumpus.png", create_wumpus_sprite, S)
        self.img_gold   = load_image("gold.png",   create_gold_sprite,   S - 10)
        self.img_pit    = load_image("pit.png",     create_pit_sprite,    S + 10)
        self.img_arrow  = load_image("arrow.png",   create_arrow_sprite,  36)
        self.img_agent  = load_image("agent.png",   create_agent_sprite,  S)

    def _blit_center(self, img, cx, cy):
        """Blit une image centrée sur les coordonnées (cx, cy) en pixels."""
        self.screen.blit(img, (cx - img.get_width()//2, cy - img.get_height()//2))

    def _cell_center(self, x, y):
        return x * CELL_SIZE + CELL_SIZE//2, y * CELL_SIZE + CELL_SIZE//2

    def draw_grid(self):
        agent = self.world.agent

        for y in range(GRID_SIZE):
            for x in range(GRID_SIZE):
                rect = pygame.Rect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE)
                cx, cy = self._cell_center(x, y)
                cell_type = self.world.grid[y][x]
                visited   = (x,y) in agent.visited

                # ── Fond ──────────────────────────────────────────────────
                if visited:
                    bg = GRAY
                elif (x,y) in agent.safe_cells:
                    bg = (190, 215, 190)
                else:
                    bg = DARK_GRAY
                pygame.draw.rect(self.screen, bg, rect)

                # ── Contenu — TOUJOURS visible pour le spectateur ─────────
                # (l'agent ne sait pas ce qu'il y a dans les cases non visitées,
                #  mais le spectateur, lui, voit tout)

                if cell_type == CellType.PIT:
                    self._blit_center(self.img_pit, cx, cy)

                if cell_type == CellType.WUMPUS:
                    if self.world.wumpus_alive:
                        self._blit_center(self.img_wumpus, cx, cy)
                    else:
                        # Wumpus mort : sprite + croix rouge
                        self._blit_center(self.img_wumpus, cx, cy)
                        sz = 24
                        pygame.draw.line(self.screen, RED, (cx-sz, cy-sz), (cx+sz, cy+sz), 5)
                        pygame.draw.line(self.screen, RED, (cx+sz, cy-sz), (cx-sz, cy+sz), 5)

                if cell_type == CellType.GOLD and not agent.has_gold:
                    self._blit_center(self.img_gold, cx, cy)

                # ── Perceptions (cases visitées) ───────────────────────────
                if visited:
                    perceptions = self.world.get_perceptions(x, y)
                    py = y * CELL_SIZE + 4
                    if perceptions['breeze']:
                        t = self.small_font.render("~brise~", True, BLUE)
                        self.screen.blit(t, (x*CELL_SIZE + 4, py)); py += 17
                    if perceptions['stench']:
                        t = self.small_font.render("~puant~", True, (80, 200, 80))
                        self.screen.blit(t, (x*CELL_SIZE + 4, py))

                # ── Marqueur Wumpus probable (cases non visitées) ──────────
                # (du point de vue de l'agent)
                if not visited and (x,y) in agent.valid_wumpus:
                    t = self.small_font.render("?W", True, ORANGE)
                    self.screen.blit(t, (x*CELL_SIZE + CELL_SIZE-28, y*CELL_SIZE+4))

                # ── Bordure ───────────────────────────────────────────────
                border_col = DARK_GREEN if (x,y)==(0,0) else BLACK
                border_w   = 4         if (x,y)==(0,0) else 2
                pygame.draw.rect(self.screen, border_col, rect, border_w)

                # ── Agent ─────────────────────────────────────────────────
                if (x,y) == (agent.x, agent.y) and agent.is_alive:
                    self._blit_center(self.img_agent, cx, cy)
                    if agent.has_arrow:
                        ax = x*CELL_SIZE + CELL_SIZE - 38
                        ay = y*CELL_SIZE + CELL_SIZE - 26
                        self.screen.blit(self.img_arrow, (ax, ay))

    def draw_info_panel(self):
        agent  = self.world.agent
        px     = WINDOW_SIZE + 10
        yo     = 10

        def txt(s, color=WHITE, big=False):
            nonlocal yo
            f = self.font if big else self.small_font
            self.screen.blit(f.render(s, True, color), (px, yo))
            yo += 28 if big else 22

        txt("═══ AI LOGIC & INFO ═══", YELLOW, big=True)
        txt(f"Position : ({agent.x}, {agent.y})  Dir : {agent.direction.name}")
        txt(f"Score    : {agent.score}")
        txt(f"Flèche   : {'Oui' if agent.has_arrow else 'Non'}   "
            f"Or : {'Oui ✓' if agent.has_gold else 'Non'}")
        txt(f"Cases sûres connues : {len(agent.safe_cells)}/16")
        txt(f"Wumpus possible : {len(agent.valid_wumpus)} case(s)")
        yo += 6
        txt("── STATUT AGENT ──", ORANGE)
        txt(agent.status, GRAY)
        yo += 6
        txt("── DERNIER MESSAGE ──", ORANGE)
        txt(self.world.message, GRAY)
        yo += 6
        txt("── CONTRÔLES ──", ORANGE)
        txt("[ESPACE] : un pas (mode manuel)")
        txt("[P]      : auto / manuel")
        txt("[+]/[-]  : vitesse auto-play")
        txt("[R]      : nouvelle partie")
        yo += 6
        txt("── PLAN D'ACTION ──", ORANGE)
        for i, act in enumerate(itertools.islice(agent.action_plan, 6)):
            txt(f"  {i+1}. {act.name}", GRAY)

        # Légende en bas
        lx, ly = WINDOW_SIZE + 10, WINDOW_HEIGHT - 90
        pygame.draw.line(self.screen, DARK_GRAY,
                         (WINDOW_SIZE, lx-60), (WINDOW_WIDTH, lx-60), 1)
        legends = [
            (self.img_wumpus, "Wumpus"),
            (self.img_gold,   "Or"),
            (self.img_pit,    "Puits"),
            (self.img_arrow,  "Flèche"),
        ]
        ox = WINDOW_SIZE + 10
        for img, label in legends:
            thumb = pygame.transform.scale(img, (28, 28))
            self.screen.blit(thumb, (ox, ly))
            t = self.small_font.render(label, True, WHITE)
            self.screen.blit(t, (ox + 30, ly + 6))
            ox += 80

    def handle_events(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE and not self.auto_play:
                    if not self.world.game_over:
                        self.world.execute_action(self.world.agent.decide_action())
                elif event.key == pygame.K_p:
                    self.auto_play = not self.auto_play
                elif event.key == pygame.K_r:
                    self.world = World()
                    self.auto_play = True
                elif event.key in (pygame.K_PLUS, pygame.K_EQUALS, pygame.K_KP_PLUS):
                    self.auto_delay = max(50, self.auto_delay - 50)
                elif event.key in (pygame.K_MINUS, pygame.K_KP_MINUS):
                    self.auto_delay = min(2000, self.auto_delay + 50)
        return True

    def run(self):
        self.auto_delay = 300   # ms entre chaque step en auto-play
        timer = 0
        running = True
        while running:
            dt      = self.clock.tick(60)
            running = self.handle_events()

            if self.auto_play and not self.world.game_over:
                timer += dt
                if timer >= self.auto_delay:
                    timer = 0
                    action = self.world.agent.decide_action()
                    self.world.execute_action(action)

            self.screen.fill((30, 30, 40))
            self.draw_grid()
            self.draw_info_panel()

            # Indicateur de vitesse
            spd = self.small_font.render(
                f"Vitesse : {self.auto_delay} ms/step  ({'AUTO' if self.auto_play else 'MANUEL'})",
                True, YELLOW)
            self.screen.blit(spd, (10, WINDOW_SIZE + 10))

            # Game Over overlay
            if self.world.game_over:
                s = pygame.Surface((WINDOW_SIZE, 60), pygame.SRCALPHA)
                s.fill((0, 0, 0, 160))
                self.screen.blit(s, (0, WINDOW_SIZE//2 - 30))
                msg = self.font.render(self.world.message + "  [R] pour rejouer", True, YELLOW)
                self.screen.blit(msg, (WINDOW_SIZE//2 - msg.get_width()//2, WINDOW_SIZE//2 - 10))

            pygame.display.flip()

        pygame.quit()
        sys.exit()


if __name__ == "__main__":
    game = Game()
    game.run()