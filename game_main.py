import math
import time
import pygame
#import sys

#defining global variables/tuples here:
G = 6.67408*(10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000)
BLUE = (000, 000, 255)
GREEN = (000, 255, 000)
ORANGE = (255, 165, 000)


class Mass:

    def __init__(self, name, mass, radius, parent_body, orbital_radius, true_anomaly=0, orbital_velo=None):
        #characteristics about the planetary mass
        self.name = name
        self.mass = mass
        self.radius = radius
        self.parent_body = parent_body #planet which the mass orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent body
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates
        
        if self.parent_body != None:
            #circular orbit assumed if no initial tangential velocity is provided
            if orbital_velo == None:
                orbital_velo = -1*math.sqrt((G*self.parent_body.mass)/(1000*self.orbital_radius)) / 1000

            self.xvelo = orbital_velo*math.cos(self.true_anomaly+math.pi/2)
            self.yvelo = orbital_velo*math.sin(self.true_anomaly+math.pi/2)
        else:
            self.xpos, self.ypos = 0, 0
            self.xvelo, self.yvelo = 0, 0


    def __repr__(self):
        return self.name
    
    def gravitational_influence(self, time_interval):
        deltax = self.xpos - self.parent_body.xpos
        deltay = self.ypos - self.parent_body.ypos
        distance = math.sqrt((deltax**2) + (deltay**2))

        #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
        force_gravity = ((G*self.mass*self.parent_body.mass) / (1000*distance)**2) / 1000
        #angles drawn from the space craft
        theta = math.atan2(deltay, deltax)

        net_xforce = force_gravity * -math.cos(theta)
        net_yforce = force_gravity * -math.sin(theta)

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	

    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval


class SpaceCraft:

    def __init__(self, name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        self.name = name
        self.mass = mass
        self.length = length
        self.initial_parent = initial_parent
        self.orbital_radius = orbital_radius #initial distance to the parent body
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates
        #circular orbit assumed if no initial tangential velocity is provided
        if orbital_velo == None:
            orbital_velo = -1*math.sqrt((G*self.initial_parent.mass)/(1000*self.orbital_radius)) / 1000 #convert m/s to km/s

        self.xvelo = orbital_velo*math.cos(self.true_anomaly+math.pi/2)
        self.yvelo = orbital_velo*math.sin(self.true_anomaly+math.pi/2)
        self.xpos, self.ypos = 0, 0


    def __repr__(self):
        return self.name

    def gravitational_influence(self, time_interval):
        #vectors reffered in [magnitude, angle] pairs
        #all angles drawn from the space craft
        gravity_vectors = []

        for body in body_list:
            deltax = self.xpos - body.xpos
            deltay = self.ypos - body.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = ((G*self.mass*body.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)

            gravity_vectors.append((force_gravity, theta))

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	

    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval

    def craftThrust(self):
        pass

    def drawCraft(self, screen, xpos, ypos, SCALE_FACTOR):
        ### input display x-y positions along with a scale factor
        #calculates the measurements of the craft given its total length
        tank_length = (1/(SCALE_FACTOR/200)) * self.length * (2/3)
        tank_width = tank_length / 3
        tank_diagonal = math.sqrt(tank_length**2 + tank_width**2)
        tail_diagonal = math.sqrt((tank_length)**2 + (2*tank_width)**2)
        
        #determines the direction of the craft based on its velocity
        nose_direction = math.atan2(self.yvelo, self.xvelo)
        nose_pos = (xpos + math.cos(nose_direction)*(tank_length*2), ypos + math.sin(nose_direction)*(tank_length*2))

        #p1-4: four edges of the tank in counterclockwise order starting from top right (when the craft is pointed up)
        p1_angle = nose_direction - math.atan2(tank_width, tank_length)
        p2_angle = nose_direction + math.atan2(tank_width, tank_length)
        p3_angle = p1_angle + math.pi
        p4_angle = p2_angle - math.pi
        
        #craft is drawn relative using polar coordinates relative to its display center
        p1_pos = (xpos + math.cos(p1_angle)*(tank_diagonal), ypos + math.sin(p1_angle)*(tank_diagonal))
        p2_pos = (xpos + math.cos(p2_angle)*(tank_diagonal), ypos + math.sin(p2_angle)*(tank_diagonal))
        p3_pos = (xpos + math.cos(p3_angle)*(tank_diagonal), ypos + math.sin(p3_angle)*(tank_diagonal))
        p4_pos = (xpos + math.cos(p4_angle)*(tank_diagonal), ypos + math.sin(p4_angle)*(tank_diagonal))

        #all points are collected 
        tank_points = (p1_pos, p2_pos, p3_pos, p4_pos)
        nose_points = (p1_pos, p2_pos, nose_pos)
        right_tail_points = (
            (xpos + math.cos(nose_direction - math.pi/2)*(tank_width), ypos + math.sin(nose_direction - math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction - 2.5)*(tail_diagonal), ypos + math.sin(nose_direction - 2.5)*(tail_diagonal)), 
            p4_pos
        )
        left_tail_points = (
            (xpos + math.cos(nose_direction + math.pi/2)*(tank_width), ypos + math.sin(nose_direction + math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction + 2.5)*(tail_diagonal), ypos + math.sin(nose_direction + 2.5)*(tail_diagonal)), 
            p3_pos
        )
        #each part of the craft is drawn as separate polygons
        pygame.draw.polygon(screen, RED, nose_points)
        pygame.draw.polygon(screen, RED, right_tail_points)
        pygame.draw.polygon(screen, RED, left_tail_points)
        pygame.draw.polygon(screen, GREY, tank_points)
        

def scale_converter(real_distance, SCALE_FACTOR):
    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)



#initialize relative center of the simulation and any planets/moons below it
#Planetary Mass: name, mass, radius, parent_planet, orbital_radius
earth = Mass("Earth", 5.97*(10**24), 6378, None, 149.6*(10**6))

body_list = [earth]#, sun)

#SpaceCraft: name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None
#if no value inputed for initial velocity, it will assume a circular orbit
starship = SpaceCraft("starship", 10, 3, earth, 7878, 0, -11)



#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, time_step, iteration_pause):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = starship #either a class object or x-y coordinates
    step_options = (0.001, 0.01, 0.1, 0.2, 1, 5) #base, 5 min orbit, 30 second, time-skip

    #starship will always have the 0 index
    target_list = [starship]
    for body in body_list:
        target_list.append(body)
    
    #sets up the initial conditions of the universe relative to some point/planet
    relative_center = None
    for body in body_list:
        if body.parent_body == None:
            relative_center = body
            body.xpos, body.ypos = 0, 0
            body.xvelo, body.xvelo = 0, 0
            break
    
    #places each planet in orbits around their parent planets, everything of which is relative to the center
    for body in body_list:
        if body != relative_center:
            body.xpos = body.orbital_radius*math.cos(body.true_anomaly) + body.parent_body.xpos
            body.ypos = body.orbital_radius*math.sin(body.true_anomaly) + body.parent_body.ypos
            body.xvelo += body.parent_body.xvelo
            body.yvelo += body.parent_body.yvelo

    starship.xpos = starship.orbital_radius*math.cos(starship.true_anomaly) + starship.initial_parent.xpos
    starship.ypos = starship.orbital_radius*math.sin(starship.true_anomaly) + starship.initial_parent.ypos
    starship.xvelo += starship.initial_parent.xvelo
    starship.yvelo += starship.initial_parent.yvelo

    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    i = 0 #iteration counter to help understand the speed of each iteration
    #event loop
    running = True
    while running:
        i += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            if event.type == pygame.KEYDOWN:
                
                #zoom in or out using up and down keys, respectively
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.8 * SCALE_FACTOR)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.2 * SCALE_FACTOR)
                #switch the target object
                if event.key == pygame.K_q:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)-1]   
                #change the simulation speed
                if event.key == pygame.K_z:
                    if step_options.index(time_step)+1 != len(step_options):
                        time_step = step_options[step_options.index(time_step)+1]
                if event.key == pygame.K_x:
                    if step_options.index(time_step) > 0:
                        time_step = step_options[step_options.index(time_step)-1]
                           
        #calculate gravity's influence on each orbiting body
        for body in body_list:
            if body != relative_center:
                body.gravitational_influence(time_step)
                body.deltaPosition(time_step)

        starship.gravitational_influence(time_step)
        starship.deltaPosition(time_step)
        
        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for body in body_list:
            dp_position = (DP_CENTER_X + scale_converter(body.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(body.ypos, SCALE_FACTOR))
            #draw.circle(surface, color, center, radius)
            pygame.draw.circle(screen, WHITE, dp_position, scale_converter(body.radius, SCALE_FACTOR))
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_pos = (DP_CENTER_X + scale_converter(starship.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(starship.ypos, SCALE_FACTOR))
        starship.drawCraft(screen, craft_dp_pos[0], craft_dp_pos[1], SCALE_FACTOR)
        #craft triangle marker
        pygame.draw.polygon(screen, RED, (craft_dp_pos, (craft_dp_pos[0]+5, craft_dp_pos[1]-10), (craft_dp_pos[0]-5, craft_dp_pos[1]-10)))
        
        pygame.display.set_caption(str(i))
        pygame.display.update()
        time.sleep(iteration_pause)

#parameters: real width included in the display window, iteration pause interval
main(1000, 1000, 2000000, 0.01, 0.01)